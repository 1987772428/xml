package com.ag.xml.controller;

import com.ag.xml.model.Br;
import com.ag.xml.model.Hunter;
import com.ag.xml.model.Logs;
import com.ag.xml.model.Tr;
import com.ag.xml.service.BrService;
import com.ag.xml.service.HunterService;
import com.ag.xml.service.LogsService;
import com.ag.xml.service.TrService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/xml")
public class XmlController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleDateFormat getDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 目标目录
    private List<File> folderList = new ArrayList<File>();

    // 目标xml文件
    private List<File> fileList = new ArrayList<File>();

    @Value("${xml.path}")
    private String path;

    @Value("${xml.thread.num}")
    private Integer threadNum;

    @Value("${xml.awaitTime}")
    private Integer awaitTime;

    @Autowired
    BrService brService;

    @Autowired
    TrService trService;

    @Autowired
    HunterService hunterService;

    @Autowired
    LogsService logsService;

    /**
     * 解析所有平台最新12个xml文件入库
     * */
//    @RequestMapping("/list")
    public String list()
    {
        // 获取xml目录
        this.getFolder(path);

        if (folderList.isEmpty()) {
            logger.error(path + "，空目录");
            return "空目录";
        }

//        folderList.stream().forEach(System.out::println);
        // 接口文档说明必须读取最新12个xml更新数据
        int n;
        // 遍历所有目录
        String paths;
        File[] files;
        File f;
        for (File folder : folderList) {
            //
            paths = folder.getPath();
            n = 0;
            // 获取目录下的所有文件或文件夹
            files = folder.listFiles();
            // 如果目录为空，继续下一个
            if (files == null) {
                logger.warn(paths + "，空目录");
                continue;
            }
            // 遍历，目录下的所有文件
            for (int i = files.length - 1; i>=0; i--) {
                f = files[i];
                if (n >= 12) continue;
                if (f.isFile() && f.getName().endsWith(".xml")) {
                    fileList.add(f);
                    n++;
                }
            }
        }
        // 释放文件夹列表
        folderList.clear();

        if (fileList.isEmpty()) {
            logger.error("找不到xml文件");
            return "找不到xml文件";
        }

//        fileList.stream().forEach(System.out::println);
        ////////////////////////////     多线程
        // 总的xml数量
        int fileListSize = fileList.size();
        // 需要开启的线程数量
        int nThreads = (fileListSize % threadNum) == 0 ? (fileListSize / threadNum) : (fileListSize / threadNum) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>(nThreads);
        // 每个线程读取的xml结束位置，如果超过总的xml个数，则读取到最后一个xml结束
        int end;
        for (int i = 0; i < nThreads; i++) {
            end = threadNum * (i + 1);
            if (end > fileListSize) end = fileListSize;
            final List<File> threadFileList = fileList.subList(threadNum * i, end);
            final int j = i;
            Callable<Integer> task = () -> {
                this.updateXml(threadFileList, j);
                return 1;
            };
            futures.add(executorService.submit(task));
        }
        // 超时时间
        try {
            // 向学生传达“问题解答完毕后请举手示意！”
            executorService.shutdown();
            // (所有的任务都结束的时候，返回TRUE)
            if (!executorService.awaitTermination(awaitTime, TimeUnit.MILLISECONDS)) {
                // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                executorService.shutdownNow();
            } else {
                // 释放xml文件列表
                fileList.clear();
            }
        } catch (InterruptedException e) {
            // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
            executorService.shutdownNow();
            // 释放xml文件列表
            fileList.clear();
        }

        if (!futures.isEmpty()) {
            futures.clear();
        }
        logger.info("xml解析完成");

        return "ok";
    }

    @RequestMapping(value = "/replace", method = {RequestMethod.GET})
    @ResponseBody
    /**
     * 补丢失的数据，每次一行
     * @param file  String      xml文件，E:/xml/AGIN/20180910/201809100000.xml
     *              路径禁止使用：E:\xml\AGIN\20180910\201809100000.xml 格式，参数安全性问题
     * @param line  int         丢失的行号
     */
//    public String replace(String file, Integer line)
    public String replace(HttpServletRequest request)
    {
        String file = request.getParameter("file");
        Integer line = 0;
        String log = "file - " + file + "，line - " + request.getParameter("line");
        // 验证行号
        try {
            line = Integer.parseInt(request.getParameter("line"));
        } catch (Exception e) {
            logger.error("line参数错误：" + request.getParameter("line"));
        }
        // 参数不能为空
        if (file.equals("") || line.equals(0)) {
            logger.error("参数错误：" + log);
            return "参数错误";
        }
        System.out.println(file);

        // 验证是否输入xml路径
        File f = new File(file);
        if (!f.isFile() || !f.getName().endsWith(".xml")) {
            logger.error("错误xml文件路径：" + file);
            return "错误xml文件路径";
        }

        // 创建SAXReader对象
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            // 参数
            List<String> list;
            // 返回(skip)行之后的数据， 不包括skip行
            list = reader.lines()
                    .skip(line - 1)
                    .limit(1)
                    .collect(Collectors.toList());
            Br br = new Br();
            Tr tr = new Tr();
            Hunter hunter = new Hunter();
            if (list.size() > 0) {
                for (String temp : list) {
                    //
                    this.insert(temp, log, getDateFormat, br, tr, hunter);
                }
            } else {
                return "行号不存在";
            }
            reader.close();
        } catch (Exception e) {
            logger.error(file + "，文件打开失败");
        }

        return log + "，更新成功";
    }

    /**
     * 获取所有为日期的文件夹
     * @param   path    string      名称不为日期的文件夹路径
     * @return  files   list        名称为日期的文件夹集
     * */
    private void getFolder(String path)
    {
        File file = new File(path);
        // 获取目录下的所有文件或文件夹
        File[] files = file.listFiles();
        // 如果目录为空，添加日志
        if (files == null) {
            logger.warn(path + "，空目录");
        } else {
            // 当前路径目标文件夹
            File folder = null;
            // 遍历，目录下的所有文件
            for (File f : files) {
                if (f.isDirectory()) {
                    // 判断文件夹名称是否为纯数字
                    Pattern pattern = Pattern.compile("[0-9]*");
                    Boolean number = pattern.matcher(f.getName()).matches();
                    if (number) {
                        folder = f;
                    } else {
                        this.getFolder(f.getAbsolutePath());
                    }
                }
            }
            //
            if (folder != null) {
                folderList.add(folder);
            }
        }
    }

    /**
     * 解析xml文件内容，并入库
     * @param files     List    等待解析的xml文件列表
     * @param thread    int     当前处理线程
     */
    private void updateXml(List<File> files, Integer thread)
    {
        logger.info("Thread：" + thread + "线程开始运行");
//        files.stream().forEach(System.out::println);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String log;
        Br br = new Br();
        Tr tr = new Tr();
        Hunter hunter = new Hunter();
        // xml新增的行数
        int size;
        // 每次开始读取行数
        int line;
        // 获取上一次读取的位置（行数）
        int selectLine;
        // 更新日志
        Logs logs = new Logs();
        // 创建SAXReader对象
        BufferedReader reader = null;
        // 参数
        List<String> list;
        for (File f1 : files) {
            // 读取xml文件内容
            try {
                //
                line = 0;
                selectLine = logsService.selectLine(f1.getPath().replaceAll("\\\\", "/"), f1.getName());
                if (selectLine > 0) {
                    line = selectLine;
                }
                //
                reader = new BufferedReader(new FileReader(f1));
                // 返回(skip)行之后的数据， 不包括skip行
                list = reader.lines()
                        .skip(line)
                        .collect(Collectors.toList());
                // 统计新增的行数
                size = list.size();
                if (size > 0) {
                    logger.warn("Thread：" + thread + "，" + f1 + "，开始更新数据");
                    for (String temp : list) {
                        // 当前读取行号
                        line++;
                        // 日志
                        log = f1.getPath() + "，line" + line + ":" + temp;
                        //
                        this.insert(temp, log, dateFormat, br, tr, hunter);
                    }

                    // 记录本次读取的位置
                    logs.setFilename(f1.getName());
                    logs.setLine(line);
                    logs.setPath(f1.getPath().replaceAll("\\\\", "/"));
                    logs.setUpdatetime(new Date());
                    logsService.addLogs(logs);
                } else {
                    logger.warn("Thread：" + thread + "，" + f1 + "，无新数据");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 关闭
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 入库
     * @param   temp    String                  每行文本内容
     * @param   log     String                  日志内容
     * @param   dateFormat  SimpleDdateFormat   时间格式（多线程不安全）
     * */
    private void insert(String temp, String log, SimpleDateFormat dateFormat, Br br, Tr tr, Hunter hunter)
    {
        try {
            // String 转 Document
            Document document = DocumentHelper.parseText(temp);
            Element root = document.getRootElement();
            String platformType = root.attributeValue("platformType");
            String playerName = root.attributeValue("playerName");
            String dataType, ic, account;
            // 根据不同平台截取
            switch (platformType) {
                case "AGIN":
                case "XIN":
                case "HUNTER":
                    ic = playerName.substring(0, 3);
                    account = playerName.substring(7);
                    break;
                case "BBIN":
                case "OG":
                case "HG":
                case "XTD":
                case "NMG":
                case "MG":
                    ic = playerName.substring(3, 6);
                    account = playerName.substring(10);
                    break;
                default:
                    ic = playerName.substring(2, 5);
                    account = playerName.substring(9);
                    break;
            }
            dataType = root.attributeValue("dataType");
            switch (dataType) {
                case "BR":
                case "EBR":
                case "HBR":
                    br.setDataType(dataType);
                    br.setBillNo(root.attributeValue("billNo"));
                    br.setPlayerName(playerName);
                    br.setGameCode(root.attributeValue("gameCode"));
                    br.setNetAmount(root.attributeValue("netAmount"));
                    br.setBetTime(dateFormat.parse(root.attributeValue("betTime")));
                    br.setGameType(root.attributeValue("gameType"));
                    br.setBetAmount(root.attributeValue("betAmount"));
                    br.setValidBetAmount(root.attributeValue("validBetAmount"));
                    br.setFlag(root.attributeValue("flag"));
                    br.setPlayType(root.attributeValue("playType"));
                    br.setTableCode(root.attributeValue("tableCode"));
                    br.setLoginIP(root.attributeValue("loginIP"));
                    br.setPlatformType(platformType);
                    br.setRound(root.attributeValue("round"));
                    br.setBeforeCredit(root.attributeValue("beforeCredit"));
                    if (dataType.equals("HBR")) {
                        br.setDeviceType("0");
                    } else {
                        br.setDeviceType(root.attributeValue("deviceType"));
                    }
                    br.setIc(ic);
                    br.setAccount(account);

                    // 入库
                    try {
                        brService.addBr(br);
                        logger.info(log + "，更新成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(log + "，插入失败");
                    }
                    break;
                case "TR":
                case "HTR":
                    tr.setDataType(dataType);
                    tr.setTransferId(root.attributeValue("transferId"));
                    if (platformType.equals("BBIN")) {
                        tr.setTradeNo(root.attributeValue("transferId"));
                    } else {
                        tr.setTradeNo(root.attributeValue("tradeNo"));
                    }
                    tr.setPlatformType(platformType);
                    tr.setPlayerName(playerName);
                    tr.setTransferType(root.attributeValue("transferType"));
                    tr.setTransferAmount(root.attributeValue("transferAmount"));
                    tr.setPreviousAmount(root.attributeValue("previousAmount"));
                    tr.setCurrentAmount(root.attributeValue("currentAmount"));
                    tr.setIP(root.attributeValue("IP"));
                    tr.setCreationTime(dateFormat.parse(root.attributeValue("creationTime")));
                    tr.setGameCode(root.attributeValue("gameCode"));
                    tr.setIc(ic);
                    tr.setAccount(account);

                    // 入库
                    try {
                        trService.addTr(tr);
                        logger.info(log + "，更新成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(log + "，插入失败");
                    }
                    break;
                case "HSR":
                    hunter.setDataType(dataType);
                    hunter.setTradeNo(root.attributeValue("tradeNo"));
                    hunter.setPlatformType(platformType);
                    hunter.setPlayerName(playerName);
                    hunter.setSceneStartTime(dateFormat.parse(root.attributeValue("SceneStartTime")));
                    hunter.setSceneEndTime(dateFormat.parse(root.attributeValue("SceneEndTime")));
                    hunter.setRoomid(root.attributeValue("Roomid"));
                    hunter.setRoombet(root.attributeValue("Roombet"));
                    hunter.setCost(root.attributeValue("Cost"));
                    hunter.setEarn(root.attributeValue("Earn"));
                    hunter.setJackpotcomm(root.attributeValue("Jackpotcomm"));
                    hunter.setTransferAmount(root.attributeValue("transferAmount"));
                    hunter.setPreviousAmount(root.attributeValue("previousAmount"));
                    hunter.setCurrentAmount(root.attributeValue("currentAmount"));
                    hunter.setFlag(root.attributeValue("flag"));
                    hunter.setCreationTime(dateFormat.parse(root.attributeValue("creationTime")));
                    hunter.setIc(ic);
                    hunter.setAccount(account);

                    // 入库
                    try {
                        hunterService.addHunter(hunter);
                        logger.info(log + "，更新成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(log + "，插入失败");
                    }
                    break;
                default:
                    logger.error(log + "，数据类型不存在，请新增该类型数据入库规则");
                    break;
            }
        } catch (Exception e) {
            logger.error(log + "获取文本内容失败");
        }
    }

}
