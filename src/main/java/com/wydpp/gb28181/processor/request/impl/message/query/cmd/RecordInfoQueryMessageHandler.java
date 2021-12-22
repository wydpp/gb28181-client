package com.wydpp.gb28181.processor.request.impl.message.query.cmd;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.ISIPCommander;
import com.wydpp.gb28181.processor.request.SIPRequestProcessorParent;
import com.wydpp.gb28181.processor.request.impl.message.IMessageHandler;
import com.wydpp.gb28181.processor.request.impl.message.query.QueryMessageHandler;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.header.FromHeader;
import javax.sip.message.Response;

/**
 * 设备音视频文件检索指令
 *
 * @author wydpp
 */
@Component
public class RecordInfoQueryMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(RecordInfoQueryMessageHandler.class);

    private final String cmdType = "RecordInfo";

    @Autowired
    private QueryMessageHandler queryMessageHandler;

    @Autowired
    private ISIPCommander sipCommander;

    @Override
    public void afterPropertiesSet() throws Exception {
        queryMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, SipDevice sipDevice, Element rootElement) {
    }

    @Override
    public void handForPlatform(RequestEvent evt, SipPlatform sipPlatform, SipDevice sipDevice, Element rootElement) {
        logger.info("接收到" + cmdType + "查询消息");
        try {
            // 回复200 OK
            responseAck(evt, Response.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FromHeader fromHeader = (FromHeader) evt.getRequest().getHeader(FromHeader.NAME);
        Element snElement = rootElement.element("SN");
        String sn = snElement.getText();
        sipCommander.recordInfoResponse(sipPlatform, sipDevice, fromHeader.getTag(), rootElement);
    }
}
