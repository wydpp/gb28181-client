package com.wydpp.gb28181.runner;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.SIPCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PlatformRegisterRunner implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(PlatformRegisterRunner.class);

    @Autowired
    private SIPCommander sipCommander;

    @Autowired
    private SipPlatform sipPlatform;

    @Autowired
    private SipDevice sipDevice;

    @Override
    public void run(String... args) {
        if (StringUtils.hasText(sipPlatform.getServerIP()) && sipDevice.isNeedRegister()) {
            boolean result = sipCommander.register(sipPlatform, sipDevice);
            logger.info("设备注册消息发送{}", result ? "成功" : "失败");
        }
    }
}
