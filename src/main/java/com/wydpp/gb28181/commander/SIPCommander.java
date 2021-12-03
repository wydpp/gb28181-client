package com.wydpp.gb28181.commander;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.event.SipSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.header.CallIdHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.UUID;

/**
 * @description:设备能力接口
 * @author: wydpp
 * @date: 2021年12月2日
 */
@Component
public class SIPCommander implements ISIPCommander {

    private Logger logger = LoggerFactory.getLogger(SIPCommander.class);

    @Autowired
    private SIPRequestHeaderPlatformProvider headerProviderPlatformProvider;

    @Autowired
    private SipSubscribe sipSubscribe;

    @Lazy
    @Autowired
    private SipProvider udpSipProvider;

    public boolean register(SipPlatform sipPlatform, SipDevice sipDevice, SipSubscribe.Event okEvent) {
        return register(sipPlatform, sipDevice, null, null, okEvent);
    }

    public boolean register(SipPlatform sipPlatform, SipDevice sipDevice, String callId, WWWAuthenticateHeader www, SipSubscribe.Event okEvent) {
        String tm = Long.toString(System.currentTimeMillis());
        Request request = null;
        CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
        if (www == null) {
            try {
                request = headerProviderPlatformProvider.createRegisterRequest(sipPlatform, sipDevice, 1L, "FromRegister" + tm, null, callIdHeader);
            } catch (Exception e) {
                logger.error("createRegisterRequest error!", e);
            }
        } else {
            try {
                callIdHeader.setCallId(callId);
                request = headerProviderPlatformProvider.createRegisterRequest(sipPlatform, sipDevice, "FromRegister" + tm, null, www, callIdHeader);
            } catch (Exception e) {
                logger.error("createRegisterRequest error!", e);
            }
        }
        if (request != null) {
            if (okEvent != null) {
                sipSubscribe.addOkSubscribe(callIdHeader.getCallId(), okEvent);
            }
            try {
                logger.info("要发送的注册消息:\n{}", request);
                udpSipProvider.sendRequest(request);
                return true;
            } catch (SipException e) {
                logger.error("sendRequest error!", e);
            }
        }
        return false;
    }

    @Override
    public boolean unRegister(SipPlatform sipPlatform, SipDevice sipDevice, SipSubscribe.Event event) {
        String tm = Long.toString(System.currentTimeMillis());
        Request request = null;
        CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
        try {
            request = headerProviderPlatformProvider.createUnRegisterRequest(sipPlatform, sipDevice, 1L, "FromRegister" + tm, null, callIdHeader);
        } catch (Exception e) {
            logger.error("createRegisterRequest error!", e);
        }
        if (request != null) {
            sipSubscribe.addOkSubscribe(callIdHeader.getCallId(), event);
            try {
                logger.info("要发送的注销消息:\n{}", request);
                udpSipProvider.sendRequest(request);
                return true;
            } catch (SipException e) {
                logger.error("sendRequest error!", e);
            }
        }
        return false;
    }

    @Override
    public boolean unRegister(SipPlatform sipPlatform, SipDevice sipDevice, String callId, WWWAuthenticateHeader www, SipSubscribe.Event event){
        String tm = Long.toString(System.currentTimeMillis());
        Request request = null;
        CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
        try {
            callIdHeader.setCallId(callId);
            request = headerProviderPlatformProvider.createUnRegisterRequest(sipPlatform, sipDevice, "FromRegister" + tm, null, www, callIdHeader);
        } catch (Exception e) {
            logger.error("createRegisterRequest error!", e);
        }
        if (request != null) {
            sipSubscribe.addOkSubscribe(callIdHeader.getCallId(), event);
            try {
                logger.info("要发送的注销消息:\n{}", request);
                udpSipProvider.sendRequest(request);
                return true;
            } catch (SipException e) {
                logger.error("sendRequest error!", e);
            }
        }
        return false;
    }

    @Override
    public String keepalive(SipPlatform sipPlatform, SipDevice sipDevice, SipSubscribe.Event okEvent) {
        String callId = null;
        try {
            StringBuffer keepaliveXml = new StringBuffer(200);
            keepaliveXml.append("<?xml version=\"1.0\"?>\r\n");
            keepaliveXml.append("<Notify>\r\n");
            keepaliveXml.append("<CmdType>Keepalive</CmdType>\r\n");
            keepaliveXml.append("<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
            keepaliveXml.append("<DeviceID>" + sipDevice.getDeviceId() + "</DeviceID>\r\n");
            keepaliveXml.append("<Status>OK</Status>\r\n");
            keepaliveXml.append("</Notify>\r\n");
            CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
            Request request = headerProviderPlatformProvider.createKeetpaliveMessageRequest(
                    sipPlatform,
                    sipDevice,
                    keepaliveXml.toString(),
                    "z9hG4bK-" + UUID.randomUUID().toString().replace("-", ""),
                    UUID.randomUUID().toString().replace("-", ""),
                    null,
                    callIdHeader);
            logger.info("要发送的心跳消息:\n{}", request);
            sipSubscribe.addOkSubscribe(callIdHeader.getCallId(), okEvent);
            udpSipProvider.sendRequest(request);
            callId = callIdHeader.getCallId();
        } catch (ParseException | InvalidArgumentException | SipException e) {
            logger.error("心跳消息发送异常!", e);
        }
        return callId;
    }

}
