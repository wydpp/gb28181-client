package com.wydpp.gb28181.schedule;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.runner.PlatformRegisterRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DeviceRegisterSchedule {

    private Logger logger = LoggerFactory.getLogger(DeviceRegisterSchedule.class);

    @Autowired
    private SipDevice sipDevice;

    @Autowired
    private PlatformRegisterRunner platformRegisterRunner;

    @Scheduled(initialDelay = 3, fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void checkSipDeviceStatus() {
        if (sipDevice.isNeedRegister() && sipDevice.getRegisterTime() != null) {
            int expires = sipDevice.getExpires() * 1000;
            long registerDate = sipDevice.getRegisterTime();
            if (System.currentTimeMillis() - registerDate >= expires) {
                platformRegisterRunner.run(null);
            }
        }
    }
}
