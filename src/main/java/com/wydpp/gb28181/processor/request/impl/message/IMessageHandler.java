package com.wydpp.gb28181.processor.request.impl.message;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import org.dom4j.Element;

import javax.sip.RequestEvent;

public interface IMessageHandler {
    /**
     * 处理来自设备的信息
     *
     * @param evt
     * @param sipDevice
     */
    void handForDevice(RequestEvent evt, SipDevice sipDevice, Element element);

    /**
     * 处理来自平台的信息
     *
     * @param evt
     * @param sipPlatform
     */
    void handForPlatform(RequestEvent evt, SipPlatform sipPlatform, Element element);
}
