package com.wydpp.gb28181.processor.request.impl;

import com.wydpp.gb28181.commander.FfmpegCommander;
import com.wydpp.gb28181.commander.ISIPCommander;
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
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;
import java.text.ParseException;

/**
 * SIP命令类型： BYE请求
 */
@Component
public class ByeRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

    private final Logger logger = LoggerFactory.getLogger(ByeRequestProcessor.class);
    private final String method = "BYE";

    @Autowired
    private ISIPCommander cmder;

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private SipSubscribe sipSubscribe;

    @Autowired
    private FfmpegCommander ffmpegCommander;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addRequestProcessor(method, this);
    }

    /**
     * 处理BYE请求
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        try {
            responseAck(evt, Response.OK);
            Dialog dialog = evt.getDialog();
            //如果有推流，则关闭
            ffmpegCommander.stopPushStream(dialog.getCallId().getCallId());
        } catch (SipException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
