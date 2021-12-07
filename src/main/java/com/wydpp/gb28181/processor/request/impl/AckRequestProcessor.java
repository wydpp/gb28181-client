package com.wydpp.gb28181.processor.request.impl;


import com.wydpp.gb28181.event.SipSubscribe;
import com.wydpp.gb28181.processor.SIPProcessorObserver;
import com.wydpp.gb28181.processor.request.ISIPRequestProcessor;
import com.wydpp.gb28181.processor.request.SIPRequestProcessorParent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.RequestEvent;

/**
 * SIP命令类型： ACK请求
 */
@Component
public class AckRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(AckRequestProcessor.class);
    private String method = "ACK";

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private SipSubscribe sipSubscribe;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addRequestProcessor(method, this);
    }

    /**
     * 处理  ACK请求
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        Dialog dialog = evt.getDialog();
        if (dialog == null) return;
        if (dialog.getState() == DialogState.CONFIRMED){
            sipSubscribe.publishAckEvent(evt);
        }
    }
}
