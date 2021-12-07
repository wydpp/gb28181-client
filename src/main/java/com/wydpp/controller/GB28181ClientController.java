package com.wydpp.controller;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.FfmpegCommander;
import com.wydpp.gb28181.commander.SIPCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/gb28181")
public class GB28181ClientController {

    private final Logger logger = LoggerFactory.getLogger(GB28181ClientController.class);

    @Autowired
    private SIPCommander sipCommander;

    @Autowired
    private SipPlatform sipPlatform;

    @Autowired
    private SipDevice sipDevice;

    @Autowired
    private FfmpegCommander ffmpegCommander;

    @PutMapping(path = "/register")
    public DeferredResult<String> register() {
        DeferredResult<String> result = new DeferredResult<>(1000 * 5L);
        result.onTimeout(() -> {
            result.setResult("注册超时!");
        });
        sipDevice.setNeedRegister(true);
        boolean sendResult = sipCommander.register(sipPlatform, sipDevice, eventResult -> {
            long time = System.currentTimeMillis();
            sipDevice.setRegisterTime(time);
            sipDevice.setKeepaliveTime(time);
            sipDevice.setOnline(true);
            result.setResult("注册成功!");
        });
        if (!sendResult) {
            result.setResult("注册指令发送失败!");
        }
        return result;
    }

    @PutMapping(path = "/unRegister")
    public DeferredResult<String> unRegister() {
        DeferredResult<String> result = new DeferredResult<>(1000 * 5L);
        result.onTimeout(() -> {
            result.setResult("注销超时!");
        });
        sipDevice.setNeedRegister(false);
        boolean sendResult = sipCommander.unRegister(sipPlatform, sipDevice, eventResult -> {
            sipDevice.setOnline(false);
            ffmpegCommander.stopAllPushStream();
            result.setResult("设备注销成功!callId=" + eventResult.callId);
        });
        if (!sendResult) {
            result.setResult("注销指令发送失败!");
        }
        return result;
    }

    @PutMapping(path = "/closePush")
    public String closePush(@RequestParam(name = "callId",required = false)String callId) {
        ffmpegCommander.stopPushStream(callId);
        return "停止推流成功!";
    }

}
