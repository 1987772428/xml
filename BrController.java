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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/xml")
public class BrController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleDateFormat getDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 目标目录
    private List<File> folderList = new ArrayList<File>();

    // 目标xml文件
    private List<File> fileList = new ArrayList<File>();

    @Value("${xml.path}")
    private String path;

    @Autowired
    BrService brService;

    @Autowired
    TrService trService;

    @Autowired
    HunterService hunterService;

    @Autowired
    LogsService logsService;

    @RequestMapping("/list")
    public String list()
    {
        // 获取xml目录
        this.getFolder(path);

//        folderList.stream().forEach(System.out::println);
        // 接口文档说明必须读取最新12个xml更新数据
        int n;
        // 遍历所有目录
        String path;
        File[] files;
        File f;
        for (File folder : folderList) {
            //
            path = folder.getPath();
            n = 0;
            // 获取目录下的所有文件或文件夹
            files = folder.listFiles();
            // 如果目录为空，继续下一个
            if (files == null) {
                logger.warn(path + "，空目录");
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

//        fileList.stream().forEach(System.out::println);
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
        Document document;
        Element root;
        String platformType, playerName, dataType, ic = null, account = null;
        for (File f1 : fileList) {
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
                    for (String temp : list) {
                        // 当前读取行号
                        line++;
                        // 日志
                        log = f1.getPath() + "，line" + line + ":" + temp;

                        // String 转 Document
                        document = DocumentHelper.parseText(temp);
                        root = document.getRootElement();
                        platformType = root.attributeValue("platformType");
                        playerName = root.attributeValue("playerName");
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
                                br.setBetTime(getDateFormat.parse(root.attributeValue("betTime")));
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
                                tr.setCreationTime(getDateFormat.parse(root.attributeValue("creationTime")));
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
                                hunter.setSceneStartTime(getDateFormat.parse(root.attributeValue("SceneStartTime")));
                                hunter.setSceneEndTime(getDateFormat.parse(root.attributeValue("SceneEndTime")));
                                hunter.setRoomid(root.attributeValue("Roomid"));
                                hunter.setRoombet(root.attributeValue("Roombet"));
                                hunter.setCost(root.attributeValue("Cost"));
                                hunter.setEarn(root.attributeValue("Earn"));
                                hunter.setJackpotcomm(root.attributeValue("Jackpotcomm"));
                                hunter.setTransferAmount(root.attributeValue("transferAmount"));
                                hunter.setPreviousAmount(root.attributeValue("previousAmount"));
                                hunter.setCurrentAmount(root.attributeValue("currentAmount"));
                                hunter.setFlag(root.attributeValue("flag"));
                                hunter.setCreationTime(getDateFormat.parse(root.attributeValue("creationTime")));
                                hunter.setIc(ic);
                                hunter.setAccount(account);

                                // 入库
                                try {
                                    hunterService.addHunter(hunter);
//                                    logger.info(log + "，更新成功");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    logger.error(log + "，插入失败");
                                }
                                break;
                            default:
                                logger.warn(log + "，数据类型不存在，请新增该类型数据入库规则");
                                break;
                        }

                    }

                    // 记录本次读取的位置
                    logs.setFilename(f1.getName());
                    logs.setLine(line);
                    logs.setPath(f1.getPath().replaceAll("\\\\", "/"));
                    logs.setUpdatetime(new Date());
                    logsService.addLogs(logs);
                } else {
                    logger.warn(f1 + "，无新数据。");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 关闭
            finally {
//                if (reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
//                }
            }

        }

        return "ok";
    }

    public void getFolder(String path)
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

}
