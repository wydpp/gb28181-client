package com.wydpp.gb28181.commander;

import com.wydpp.config.SipDeviceConfig;
import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
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

    @Lazy
    @Autowired
    private SipProvider udpSipProvider;

    public boolean register(SipPlatform sipPlatform, SipDevice sipDevice) {
        return register(sipPlatform, sipDevice, null, null);
    }

    public boolean register(SipPlatform sipPlatform, SipDevice sipDevice, String callId, WWWAuthenticateHeader www) {
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
                request = headerProviderPlatformProvider.createRegisterRequest(sipPlatform, sipDevice, "FromRegister" + tm, null, callId, www, callIdHeader);
            } catch (Exception e) {
                logger.error("createRegisterRequest error!", e);
            }
        }
        if (request != null) {
            logger.info("");
            try {
                sendRequest(request);
                return true;
            } catch (SipException e) {
                logger.error("sendRequest error!", e);
            }
        }
        return false;
    }

    @Override
    public String keepalive(SipPlatform sipPlatform,SipDevice sipDevice) {
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
                    keepaliveXml.toString(),
                    "z9hG4bK-" + UUID.randomUUID().toString().replace("-", ""),
                    UUID.randomUUID().toString().replace("-", ""),
                    null,
                    callIdHeader);
            sendRequest(request);
            callId = callIdHeader.getCallId();
        } catch (ParseException | InvalidArgumentException | SipException e) {
            e.printStackTrace();
        }
        return callId;
    }

    private void sendRequest(Request request) throws SipException {
        logger.info("要发送的sip消息:\n{}", request.toString());
        udpSipProvider.sendRequest(request);
    }

}
