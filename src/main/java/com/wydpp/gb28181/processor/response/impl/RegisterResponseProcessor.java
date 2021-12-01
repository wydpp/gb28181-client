package com.wydpp.gb28181.processor.response.impl;

import com.wydpp.gb28181.processor.SIPProcessorObserver;
import com.wydpp.gb28181.processor.response.SIPResponseProcessorAbstract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;

/**
 * @description:Register响应处理器
 * @author: swwheihei
 * @date: 2020年5月3日 下午5:32:23
 */
@Component
public class RegisterResponseProcessor extends SIPResponseProcessorAbstract {

    private Logger logger = LoggerFactory.getLogger(RegisterResponseProcessor.class);
    private String method = "REGISTER";

//	@Autowired
//	private ISIPCommanderForPlatform sipCommanderForPlatform;

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addResponseProcessor(method, this);
    }

    /**
     * 处理Register响应
     *
     * @param evt 事件
     */
    @Override
    public void process(ResponseEvent evt) {
        Response response = evt.getResponse();
        CallIdHeader callIdHeader = (CallIdHeader) response.getHeader(CallIdHeader.NAME);
        String callId = callIdHeader.getCallId();

    }

}
