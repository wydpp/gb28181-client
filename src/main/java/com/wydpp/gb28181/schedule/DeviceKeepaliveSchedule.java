package com.wydpp.gb28181.schedule;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.SIPCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class DeviceKeepaliveSchedule {

    private Logger logger = LoggerFactory.getLogger(DeviceKeepaliveSchedule.class);

    @Autowired
    private SipDevice sipDevice;

    @Autowired
    private SipPlatform sipPlatform;

    @Autowired
    private SIPCommander sipCommander;

    @Scheduled(initialDelay = 3, fixedDelay = 1,timeUnit = TimeUnit.SECONDS)
    public void checkSipDeviceStatus() {
        if (sipDevice.isOnline() && sipDevice.getKeepaliveTime() != null) {
            long expires = sipPlatform.getKeepTimeout() * 1000;
            long keepaliveTime = sipDevice.getKeepaliveTime();
            long nowTime = System.currentTimeMillis();
            if (nowTime - keepaliveTime >= expires) {
                sipCommander.keepalive(sipPlatform, sipDevice);
                sipDevice.setKeepaliveTime(nowTime);
            }
        }
    }
}
