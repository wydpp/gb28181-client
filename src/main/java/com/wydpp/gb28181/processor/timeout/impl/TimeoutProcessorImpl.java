package com.wydpp.gb28181.processor.timeout.impl;

import com.wydpp.gb28181.event.SipSubscribe;
import com.wydpp.gb28181.processor.SIPProcessorObserver;
import com.wydpp.gb28181.processor.timeout.ITimeoutProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.TimeoutEvent;
import javax.sip.header.CallIdHeader;

@Component
public class TimeoutProcessorImpl implements InitializingBean, ITimeoutProcessor {

    @Autowired
    private SIPProcessorObserver processorObserver;

    @Autowired
    private SipSubscribe sipSubscribe;

    @Override
    public void afterPropertiesSet() throws Exception {
        processorObserver.addTimeoutProcessor(this);
    }

    @Override
    public void process(TimeoutEvent event) {
        CallIdHeader callIdHeader = event.getClientTransaction().getDialog().getCallId();
        String callId = callIdHeader.getCallId();
        SipSubscribe.Event errorSubscribe = sipSubscribe.getErrorSubscribe(callId);
        SipSubscribe.EventResult<TimeoutEvent> timeoutEventEventResult = new SipSubscribe.EventResult<>(event);
        errorSubscribe.response(timeoutEventEventResult);
    }
}
