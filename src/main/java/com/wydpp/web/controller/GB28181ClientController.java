package com.wydpp.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web")
public class GB28181ClientController {

    private final Logger logger = LoggerFactory.getLogger(GB28181ClientController.class);

    @GetMapping(path = "/msg/{msg}")
    public String sendMsg(@PathVariable(name = "msg")String msg){
        logger.info("发送消息:{}",msg);
        return "ok";
    }

}
