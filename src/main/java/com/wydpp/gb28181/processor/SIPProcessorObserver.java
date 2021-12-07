package com.wydpp.gb28181.processor;

import com.wydpp.gb28181.event.SipSubscribe;
import com.wydpp.gb28181.processor.request.ISIPRequestProcessor;
import com.wydpp.gb28181.processor.response.ISIPResponseProcessor;
import com.wydpp.gb28181.processor.timeout.ITimeoutProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: SIP信令处理类观察者
 * @author: wydpp
 * @date: 2021年12月2日
 */
@Component
public class SIPProcessorObserver implements SipListener {

    private final static Logger logger = LoggerFactory.getLogger(SIPProcessorObserver.class);

    private static Map<String, ISIPRequestProcessor> requestProcessorMap = new ConcurrentHashMap<>();

    private static Map<String, ISIPResponseProcessor> responseProcessorMap = new ConcurrentHashMap<>();

    private static ITimeoutProcessor timeoutProcessor;

    @Autowired
    private SipSubscribe sipSubscribe;

    /**
     * 添加 request订阅
     *
     * @param method    方法名
     * @param processor 处理程序
     */
    public void addRequestProcessor(String method, ISIPRequestProcessor processor) {
        requestProcessorMap.put(method, processor);
    }

    /**
     * 添加 response订阅
     *
     * @param method    方法名
     * @param processor 处理程序
     */
    public void addResponseProcessor(String method, ISIPResponseProcessor processor) {
        responseProcessorMap.put(method, processor);
    }

    /**
     * 添加 超时事件订阅
     *
     * @param processor 处理程序
     */
    public void addTimeoutProcessor(ITimeoutProcessor processor) {
        this.timeoutProcessor = processor;
    }

    /**
     * 分发RequestEvent事件
     *
     * @param requestEvent RequestEvent事件
     */
    @Override
    @Async
    public void processRequest(RequestEvent requestEvent) {
        logger.info("收到请求：\n{}", requestEvent.getRequest().toString());
        String method = requestEvent.getRequest().getMethod();
        ISIPRequestProcessor sipRequestProcessor = requestProcessorMap.get(method);
        if (sipRequestProcessor == null) {
            logger.warn("不支持方法{}的request", method);
            return;
        }
        sipRequestProcessor.process(requestEvent);
    }

    /**
     * 分发ResponseEvent事件
     *
     * @param responseEvent responseEvent事件
     */
    @Override
    @Async
    public void processResponse(ResponseEvent responseEvent) {
        Response response = responseEvent.getResponse();
        ViaHeader via = (ViaHeader) response.getHeader("Via");
        int status = response.getStatusCode();
        if (((status >= 200) && (status < 300)) || status == 401) { // Success!
            if (status != 200){
                logger.info("收到{}回复,来源：{}，\n{}", status, via.getHost() + ":" + via.getPort(), responseEvent.getResponse().toString());
            }else {
                logger.info("收到{}回复,来源：{}", status, via.getHost() + ":" + via.getPort());
            }
            CSeqHeader cseqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME);
            String method = cseqHeader.getMethod();
            ISIPResponseProcessor sipRequestProcessor = responseProcessorMap.get(method);
            if (sipRequestProcessor != null) {
                sipRequestProcessor.process(responseEvent);
            }
            if (status == 200) {
                sipSubscribe.publishOkEvent(responseEvent);
            }
        } else if ((status >= 100) && (status < 200)) {
            // 增加其它无需回复的响应，如101、180等
        } else {
            logger.warn("接收到失败的response响应！status：" + status + ",message:" + response.getReasonPhrase()/* .getContent().toString()*/);
            if (responseEvent.getResponse() != null && sipSubscribe.getErrorSubscribesSize() > 0) {
                CallIdHeader callIdHeader = (CallIdHeader) responseEvent.getResponse().getHeader(CallIdHeader.NAME);
                if (callIdHeader != null) {
                    SipSubscribe.Event subscribe = sipSubscribe.getErrorSubscribe(callIdHeader.getCallId());
                    if (subscribe != null) {
                        SipSubscribe.EventResult eventResult = new SipSubscribe.EventResult(responseEvent);
                        subscribe.response(eventResult);
                    }
                }
            }
            if (responseEvent.getDialog() != null) {
                responseEvent.getDialog().delete();
            }
        }
    }

    /**
     * 向超时订阅发送消息
     *
     * @param timeoutEvent timeoutEvent事件
     */
    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        if (timeoutProcessor != null) {
            timeoutProcessor.process(timeoutEvent);
        }
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        logger.info("processIOException");
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        logger.info("processTransactionTerminated");
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        logger.info("processDialogTerminated");
        //CallIdHeader callId = dialogTerminatedEvent.getDialog().getCallId();
    }


}
