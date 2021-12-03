package com.wydpp.gb28181.schedule;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.SIPCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 注册定时任务
 */
@Component
public class DeviceRegisterSchedule {

    private Logger logger = LoggerFactory.getLogger(DeviceRegisterSchedule.class);

    @Autowired
    private SipDevice sipDevice;

    @Autowired
    private SipPlatform sipPlatform;

    @Autowired
    private SIPCommander sipCommander;

    /**
     * 设备注册时间超时定时任务
     */
    @Scheduled(initialDelay = 3, fixedDelay = 120, timeUnit = TimeUnit.SECONDS)
    public void checkSipDeviceStatus() {
        if (sipDevice.isNeedRegister() && sipDevice.getRegisterTime() != null) {
            int expires = sipDevice.getExpires() * 1000;
            long registerDate = sipDevice.getRegisterTime();
            if (System.currentTimeMillis() - registerDate >= expires - 10) {
                sipCommander.register(sipPlatform, sipDevice, eventResult -> {
                    long time = System.currentTimeMillis();
                    sipDevice.setOnline(true);
                    sipDevice.setRegisterTime(time);
                    sipDevice.setNeedRegister(true);
                    sipDevice.setKeepaliveTime(time);
                });
            }
        }
    }
}
