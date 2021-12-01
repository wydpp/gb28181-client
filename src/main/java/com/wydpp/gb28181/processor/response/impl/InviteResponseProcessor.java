package com.wydpp.gb28181.processor.response.impl;

import com.wydpp.config.SipDeviceConfig;
import com.wydpp.gb28181.SipLayer;
import com.wydpp.gb28181.processor.SIPProcessorObserver;
import com.wydpp.gb28181.processor.response.SIPResponseProcessorAbstract;
import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.stack.SIPDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;


/**
 * @description: 处理INVITE响应
 * @author: panlinlin
 * @date: 2021年11月5日 16：40
 */
@Component
public class InviteResponseProcessor extends SIPResponseProcessorAbstract {

    private final static Logger logger = LoggerFactory.getLogger(InviteResponseProcessor.class);
    private String method = "INVITE";

    @Autowired
    private SipLayer sipLayer;

    @Autowired
    private SipDeviceConfig config;


    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addResponseProcessor(method, this);
    }

    /**
     * 处理invite响应
     *
     * @param evt 响应消息
     * @throws ParseException
     */
    @Override
    public void process(ResponseEvent evt) {
        try {
            Response response = evt.getResponse();
            int statusCode = response.getStatusCode();
            // trying不会回复
            if (statusCode == Response.TRYING) {
            }
            // 成功响应
            // 下发ack
            if (statusCode == Response.OK) {
                ResponseEventExt event = (ResponseEventExt) evt;
                SIPDialog dialog = (SIPDialog) evt.getDialog();
                CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
                Request reqAck = dialog.createAck(cseq.getSeqNumber());
                SipURI requestURI = (SipURI) reqAck.getRequestURI();
                try {
                    requestURI.setHost(event.getRemoteIpAddress());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                requestURI.setPort(event.getRemotePort());
                reqAck.setRequestURI(requestURI);
                logger.info("向 " + event.getRemoteIpAddress() + ":" + event.getRemotePort() + "回复ack");
                SipURI sipURI = (SipURI) dialog.getRemoteParty().getURI();
                String deviceId = requestURI.getUser();
                String channelId = sipURI.getUser();

                dialog.sendAck(reqAck);

            }
        } catch (InvalidArgumentException | SipException e) {
            e.printStackTrace();
        }
    }

}
