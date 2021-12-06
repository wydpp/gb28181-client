package com.wydpp.gb28181.commander;

import com.wydpp.gb28181.bean.SendRtpItem;
import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.event.SipSubscribe;

import javax.sip.SipException;
import javax.sip.header.WWWAuthenticateHeader;

public interface ISIPCommander {

    boolean register(SipPlatform sipPlatform, SipDevice sipDevice, SipSubscribe.Event okEvent);

    boolean register(SipPlatform sipPlatform, SipDevice sipDevice, String callId, WWWAuthenticateHeader www, SipSubscribe.Event okEvent) throws SipException;

    boolean unRegister(SipPlatform sipPlatform, SipDevice sipDevice, SipSubscribe.Event event);

    boolean unRegister(SipPlatform sipPlatform, SipDevice sipDevice, String callId, WWWAuthenticateHeader www, SipSubscribe.Event event);

    String keepalive(SipPlatform sipPlatform, SipDevice sipDevice, SipSubscribe.Event okEvent);

    boolean catalogResponse(SipPlatform sipPlatform, SipDevice sipDevice, String sn, String fromTag);

    boolean play(SipPlatform sipPlatform, SipDevice sipDevice, SendRtpItem sendRtpItem, SipSubscribe.Event okEvent);
}
