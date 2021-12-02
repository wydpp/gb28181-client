package com.wydpp.gb28181.commander;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;

import javax.sip.SipException;
import javax.sip.header.WWWAuthenticateHeader;

public interface ISIPCommander {

    boolean register(SipPlatform sipPlatform, SipDevice sipDevice);

    boolean register(SipPlatform sipPlatform, SipDevice sipDevice, String callId, WWWAuthenticateHeader www) throws SipException;

    String keepalive(SipPlatform sipPlatform,SipDevice sipDevice);
}
