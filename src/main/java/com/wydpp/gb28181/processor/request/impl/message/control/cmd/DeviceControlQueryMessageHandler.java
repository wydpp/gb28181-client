package com.wydpp.gb28181.processor.request.impl.message.control.cmd;

import com.wydpp.gb28181.bean.Device;
import com.wydpp.gb28181.bean.ParentPlatform;
import com.wydpp.gb28181.commander.SIPCommander;
import com.wydpp.gb28181.processor.request.SIPRequestProcessorParent;
import com.wydpp.gb28181.processor.request.impl.message.IMessageHandler;
import com.wydpp.gb28181.processor.request.impl.message.control.ControlMessageHandler;
import gov.nist.javax.sip.SipStackImpl;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.RequestEvent;
import javax.sip.SipProvider;
import javax.sip.address.SipURI;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import java.util.Iterator;

import static com.wydpp.utils.XmlUtil.getText;

@Component
public class DeviceControlQueryMessageHandler extends SIPRequestProcessorParent implements InitializingBean, IMessageHandler {

    private Logger logger = LoggerFactory.getLogger(DeviceControlQueryMessageHandler.class);
    private final String cmdType = "DeviceControl";

    @Autowired
    private ControlMessageHandler controlMessageHandler;

    @Autowired
    private SIPCommander cmder;

    @Override
    public void afterPropertiesSet() throws Exception {
        //controlMessageHandler.addHandler(cmdType, this);
    }

    @Override
    public void handForDevice(RequestEvent evt, Device device, Element element) {

    }

    @Override
    public void handForPlatform(RequestEvent evt, ParentPlatform parentPlatform, Element rootElement) {}
}
