package com.wydpp.gb28181.bean;

public class ParentPlatformCatch {

    private String id;

    // 心跳未回复次数
    private int keepAliveReply;

    // 注册未回复次数
    private int registerAliveReply;

    private String callId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getKeepAliveReply() {
        return keepAliveReply;
    }

    public void setKeepAliveReply(int keepAliveReply) {
        this.keepAliveReply = keepAliveReply;
    }

    public int getRegisterAliveReply() {
        return registerAliveReply;
    }

    public void setRegisterAliveReply(int registerAliveReply) {
        this.registerAliveReply = registerAliveReply;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }
}
