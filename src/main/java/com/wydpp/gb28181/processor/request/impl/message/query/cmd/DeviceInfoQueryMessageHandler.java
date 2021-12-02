package com.wydpp.gb28181.processor.request.impl.message.query.cmd;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.processor.request.SIPRequestProcessorParent;
import com.wydpp.gb28181.processor.request.impl.message.IMessageHandler;
import com.wydpp.gb28181.processor.request.impl.message.query.QueryMessageHandler;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.FromHeader;
import javax.sip.message.Response;
import java.text.ParseException;

@Component
public class DeviceInfoQueryMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(DeviceInfoQueryMessageHandler.class);
    private final String cmdType = "DeviceInfo";

    @Autowired
    private QueryMessageHandler queryMessageHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        queryMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, SipDevice sipDevice, Element rootElement) {

    }

    @Override
    public void handForPlatform(RequestEvent evt, SipPlatform sipPlatform, Element rootElement) {
        logger.info("接收到DeviceInfo查询消息");
        FromHeader fromHeader = (FromHeader) evt.getRequest().getHeader(FromHeader.NAME);
        try {
            // 回复200 OK
            responseAck(evt, Response.OK);
        } catch (SipException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String sn = rootElement.element("SN").getText();
        //cmderFroPlatform.deviceInfoResponse(parentPlatform, sn, fromHeader.getTag());
    }
}
