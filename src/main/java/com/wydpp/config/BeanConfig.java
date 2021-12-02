package com.wydpp.config;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class BeanConfig {

    @Autowired
    private SipServerConfig sipServerConfig;

    @Autowired
    private SipDeviceConfig sipDeviceConfig;

    @Bean
    public SipPlatform sipPlatform() {
        SipPlatform sipPlatform = new SipPlatform();
        sipPlatform.setServerGBId(sipServerConfig.getId());
        sipPlatform.setServerGBDomain(sipServerConfig.getDomain());
        sipPlatform.setServerIP(sipServerConfig.getIp());
        sipPlatform.setServerPort(sipServerConfig.getPort());
        sipPlatform.setPassword(sipServerConfig.getPassword());
        sipPlatform.setDeviceGBId(sipDeviceConfig.getId());
        //使用UDP协议
        sipPlatform.setTransport("UDP");
        //注册有效期1个小时
        sipPlatform.setExpires("3600");
        sipPlatform.setKeepTimeout(sipServerConfig.getKeepaliveTimeOut());
        return sipPlatform;
    }

    @Bean
    public SipDevice sipDevice(){
        SipDevice sipDevice = new SipDevice();
        sipDevice.setDeviceId("gb28181-client-001");
        //注册有效期1个小时
        sipDevice.setExpires(3600);
        sipDevice.setIp(sipDeviceConfig.getIp());
        sipDevice.setDeviceId(sipDeviceConfig.getId());
        sipDevice.setChannelCount(1);
        sipDevice.setTransport("UDP");
        sipDevice.setName("gb28181-client");
        sipDevice.setManufacturer("wydpp");
        sipDevice.setPort(sipDeviceConfig.getPort());
        sipDevice.setModel("EMA");
        return sipDevice;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
