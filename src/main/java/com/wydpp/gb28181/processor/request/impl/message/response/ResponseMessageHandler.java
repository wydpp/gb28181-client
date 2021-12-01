package com.wydpp.gb28181.processor.request.impl.message.response;

import com.wydpp.gb28181.processor.request.impl.message.MessageHandlerAbstract;
import com.wydpp.gb28181.processor.request.impl.message.MessageRequestProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 命令类型： 请求动作的应答
 * 命令类型： 设备控制, 报警通知, 设备目录信息查询, 目录信息查询, 目录收到, 设备信息查询, 设备状态信息查询 ......
 */
@Component
public class ResponseMessageHandler extends MessageHandlerAbstract implements InitializingBean {

    private final String messageType = "Response";

    @Autowired
    private MessageRequestProcessor messageRequestProcessor;

    @Override
    public void afterPropertiesSet() throws Exception {
        messageRequestProcessor.addHandler(messageType, this);
    }
}
