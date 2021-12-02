package com.wydpp.gb28181.processor.response.impl;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.SIPCommander;
import com.wydpp.gb28181.processor.SIPProcessorObserver;
import com.wydpp.gb28181.processor.response.SIPResponseProcessorAbstract;
import com.wydpp.gb28181.runner.PlatformRegisterRunner;
import com.wydpp.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Response;
import java.util.Date;

/**
 * @description:Register响应处理器
 * @author: wydpp
 * @date: 2021年12月2日
 */
@Component
public class RegisterResponseProcessor extends SIPResponseProcessorAbstract {

    private Logger logger = LoggerFactory.getLogger(RegisterResponseProcessor.class);

    private String method = "REGISTER";

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private SIPCommander sipCommander;

    @Autowired
    private SipPlatform sipPlatform;

    @Autowired
    private SipDevice sipDevice;

    @Override
    public void afterPropertiesSet() throws Exception {
        sipProcessorObserver.addResponseProcessor(method, this);
    }

    /**
     * 处理Register响应
     * @param evt 事件
     */
    @Override
    public void process(ResponseEvent evt) {
        Response response = evt.getResponse();
        CallIdHeader callIdHeader = (CallIdHeader) response.getHeader(CallIdHeader.NAME);
        String callId = callIdHeader.getCallId();
        int statusCode = response.getStatusCode();
        String platformInfo = sipPlatform.getServerIP() + ":" + sipPlatform.getServerPort();
        if (statusCode == 401) {
            //未携带验证信息
            WWWAuthenticateHeader authorizationHeader = (WWWAuthenticateHeader) response.getHeader(WWWAuthenticateHeader.NAME);
            logger.info("向平台:{} 发送带认证信息的注册消息!", platformInfo);
            sipCommander.register(sipPlatform, sipDevice, callId, authorizationHeader);
        } else if (statusCode == 200) {
            long time = new Date().getTime();
            logger.info("设备向平台:{} 注册成功!", platformInfo);
            sipDevice.setOnline(true);
            sipDevice.setRegisterTime(time);
            sipDevice.setNeedRegister(true);
            sipDevice.setKeepaliveTime(time);
        } else {
            logger.info("设备向平台:{} 注册失败!", platformInfo);
            sipDevice.setOnline(false);
            sipDevice.setNeedRegister(false);
        }
    }


}
