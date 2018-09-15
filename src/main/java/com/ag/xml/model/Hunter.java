package com.ag.xml.model;

import java.util.Date;

public class Hunter {
    private Long id;

    private String ic;

    private String account;

    private String platformType;

    private String dataType;

    private String playerName;

    private String tradeNo;

    private String cost;

    private String earn;

    private Date creationTime;

    private String jackpotcomm;

    private String roomid;

    private String roombet;

    private Date sceneStartTime;

    private Date sceneEndTime;

    private String transferAmount;

    private String previousAmount;

    private String currentAmount;

    private String flag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic == null ? null : ic.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType == null ? null : platformType.trim();
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName == null ? null : playerName.trim();
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost == null ? null : cost.trim();
    }

    public String getEarn() {
        return earn;
    }

    public void setEarn(String earn) {
        this.earn = earn == null ? null : earn.trim();
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getJackpotcomm() {
        return jackpotcomm;
    }

    public void setJackpotcomm(String jackpotcomm) {
        this.jackpotcomm = jackpotcomm == null ? null : jackpotcomm.trim();
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid == null ? null : roomid.trim();
    }

    public String getRoombet() {
        return roombet;
    }

    public void setRoombet(String roombet) {
        this.roombet = roombet == null ? null : roombet.trim();
    }

    public Date getSceneStartTime() {
        return sceneStartTime;
    }

    public void setSceneStartTime(Date sceneStartTime) {
        this.sceneStartTime = sceneStartTime;
    }

    public Date getSceneEndTime() {
        return sceneEndTime;
    }

    public void setSceneEndTime(Date sceneEndTime) {
        this.sceneEndTime = sceneEndTime;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount == null ? null : transferAmount.trim();
    }

    public String getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(String previousAmount) {
        this.previousAmount = previousAmount == null ? null : previousAmount.trim();
    }

    public String getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(String currentAmount) {
        this.currentAmount = currentAmount == null ? null : currentAmount.trim();
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag == null ? null : flag.trim();
    }
}