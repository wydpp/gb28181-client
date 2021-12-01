package com.wydpp.gb28181.processor.request.impl.message.response.cmd;

import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.wydpp.gb28181.bean.Device;
import com.wydpp.gb28181.bean.ParentPlatform;
import com.wydpp.gb28181.processor.request.SIPRequestProcessorParent;
import com.wydpp.gb28181.processor.request.impl.message.IMessageHandler;
import com.wydpp.gb28181.processor.request.impl.message.response.ResponseMessageHandler;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;

@Component
public class AlarmResponseMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(AlarmResponseMessageHandler.class);
    private final String cmdType = "Alarm";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

//    @Autowired
//    private DeferredResultHolder deferredResultHolder;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element rootElement) {
        /*Element deviceIdElement = rootElement.element("DeviceID");
        String channelId = deviceIdElement.getText().toString();
        String key = DeferredResultHolder.CALLBACK_CMD_ALARM + device.getDeviceId() + channelId;
        JSONObject json = new JSONObject();
        XmlUtil.node2Json(rootElement, json);
        if (logger.isDebugEnabled()) {
            logger.debug(json.toJSONString());
        }
        RequestMessage msg = new RequestMessage();
        msg.setKey(key);
        msg.setData(json);
        deferredResultHolder.invokeAllResult(msg);*/
    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element element) {

    }
}
