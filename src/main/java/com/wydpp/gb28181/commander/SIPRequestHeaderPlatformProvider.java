package com.wydpp.gb28181.commander;

import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @description: 平台命令request创造器
 * @author: wydpp
 * @date: 2021年12月3日
 */
@Component
public class SIPRequestHeaderPlatformProvider {

    //@Autowired
    //private SipDeviceConfig sipConfig;

    @Autowired
    private SipFactory sipFactory;

    public Request createKeetpaliveMessageRequest(SipPlatform parentPlatform, SipDevice sipDevice, String content, String viaTag, String fromTag, String toTag, CallIdHeader callIdHeader) throws ParseException, InvalidArgumentException, PeerUnavailableException {
        Request request = null;
        // sipuri
        SipURI requestURI = sipFactory.createAddressFactory().createSipURI(parentPlatform.getServerGBId(), parentPlatform.getServerIP() + ":" + parentPlatform.getServerPort());
        // via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = sipFactory.createHeaderFactory().createViaHeader(sipDevice.getIp(), sipDevice.getPort(),
                parentPlatform.getTransport(), viaTag);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        // from
        SipURI fromSipURI = sipFactory.createAddressFactory().createSipURI(sipDevice.getDeviceId(), sipDevice.getIp() + ":" + sipDevice.getPort());
        Address fromAddress = sipFactory.createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipFactory.createHeaderFactory().createFromHeader(fromAddress, fromTag);
        // to
        SipURI toSipURI = sipFactory.createAddressFactory().createSipURI(parentPlatform.getServerGBId(), parentPlatform.getServerIP() + ":" + parentPlatform.getServerPort());
        Address toAddress = sipFactory.createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipFactory.createHeaderFactory().createToHeader(toAddress, toTag);
        // Forwards
        MaxForwardsHeader maxForwards = sipFactory.createHeaderFactory().createMaxForwardsHeader(70);
        // ceq
        CSeqHeader cSeqHeader = sipFactory.createHeaderFactory().createCSeqHeader(1L, Request.MESSAGE);
        request = sipFactory.createMessageFactory().createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        List<String> agentParam = new ArrayList<>();
        agentParam.add("wydpp");
        UserAgentHeader userAgentHeader = sipFactory.createHeaderFactory().createUserAgentHeader(agentParam);
        request.addHeader(userAgentHeader);
        ContentTypeHeader contentTypeHeader = sipFactory.createHeaderFactory().createContentTypeHeader("Application", "MANSCDP+xml");
        request.setContent(content, contentTypeHeader);
        return request;
    }


    public Request createRegisterRequest(SipPlatform platform, SipDevice sipDevice, long CSeq, String fromTag, String viaTag, CallIdHeader callIdHeader) throws ParseException, InvalidArgumentException, PeerUnavailableException {
        Request request = null;
        String sipAddress = sipDevice.getIp() + ":" + sipDevice.getPort();
        //请求行
        SipURI requestLine = sipFactory.createAddressFactory().createSipURI(platform.getServerGBId(),
                platform.getServerIP() + ":" + platform.getServerPort());
        //via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = sipFactory.createHeaderFactory().createViaHeader(platform.getServerIP(), platform.getServerPort(), platform.getTransport(), viaTag);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        //from
        SipURI fromSipURI = sipFactory.createAddressFactory().createSipURI(sipDevice.getDeviceId(), sipAddress);
        Address fromAddress = sipFactory.createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipFactory.createHeaderFactory().createFromHeader(fromAddress, fromTag);
        //to
        SipURI toSipURI = sipFactory.createAddressFactory().createSipURI(sipDevice.getDeviceId(), sipAddress);
        Address toAddress = sipFactory.createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipFactory.createHeaderFactory().createToHeader(toAddress, null);
        //Forwards
        MaxForwardsHeader maxForwards = sipFactory.createHeaderFactory().createMaxForwardsHeader(70);
        //ceq
        CSeqHeader cSeqHeader = sipFactory.createHeaderFactory().createCSeqHeader(CSeq, Request.REGISTER);
        request = sipFactory.createMessageFactory().createRequest(requestLine, Request.REGISTER, callIdHeader,
                cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
        Address concatAddress = sipFactory.createAddressFactory().createAddress(sipFactory.createAddressFactory()
                .createSipURI(sipDevice.getDeviceId(), sipAddress));
        request.addHeader(sipFactory.createHeaderFactory().createContactHeader(concatAddress));
        ExpiresHeader expires = sipFactory.createHeaderFactory().createExpiresHeader(sipDevice.getExpires());
        request.addHeader(expires);
        List<String> agentParam = new ArrayList<>();
        agentParam.add("wydpp");
        UserAgentHeader userAgentHeader = sipFactory.createHeaderFactory().createUserAgentHeader(agentParam);
        request.addHeader(userAgentHeader);
        return request;
    }

    public Request createRegisterRequest(SipPlatform parentPlatform, SipDevice sipDevice, String fromTag, String viaTag,
                                         WWWAuthenticateHeader www, CallIdHeader callIdHeader) throws ParseException, PeerUnavailableException, InvalidArgumentException {
        Request registerRequest = createRegisterRequest(parentPlatform, sipDevice, 2L, fromTag, viaTag, callIdHeader);
        String realm = www.getRealm();
        String nonce = www.getNonce();
        String scheme = www.getScheme();
        // 参考 https://blog.csdn.net/y673533511/article/details/88388138
        // qop 保护质量 包含auth（默认的）和auth-int（增加了报文完整性检测）两种策略
        String qop = www.getQop();
        SipURI requestURI = sipFactory.createAddressFactory().createSipURI(sipDevice.getDeviceId(), parentPlatform.getServerIP() + ":" + parentPlatform.getServerPort());
        String cNonce = null;
        String nc = "00000001";
        if (qop != null) {
            if ("auth".equals(qop)) {
                // 客户端随机数，这是一个不透明的字符串值，由客户端提供，并且客户端和服务器都会使用，以避免用明文文本。
                // 这使得双方都可以查验对方的身份，并对消息的完整性提供一些保护
                cNonce = UUID.randomUUID().toString();
            } else if ("auth-int".equals(qop)) {
                // TODO
            }
        }
        String HA1 = DigestUtils.md5DigestAsHex((sipDevice.getDeviceId() + ":" + realm + ":" + parentPlatform.getPassword()).getBytes());
        String HA2 = DigestUtils.md5DigestAsHex((Request.REGISTER + ":" + requestURI.toString()).getBytes());
        StringBuffer reStr = new StringBuffer(200);
        reStr.append(HA1);
        reStr.append(":");
        reStr.append(nonce);
        reStr.append(":");
        if (qop != null) {
            reStr.append(nc);
            reStr.append(":");
            reStr.append(cNonce);
            reStr.append(":");
            reStr.append(qop);
            reStr.append(":");
        }
        reStr.append(HA2);
        String RESPONSE = DigestUtils.md5DigestAsHex(reStr.toString().getBytes());
        AuthorizationHeader authorizationHeader = sipFactory.createHeaderFactory().createAuthorizationHeader(scheme);
        authorizationHeader.setUsername(sipDevice.getDeviceId());
        authorizationHeader.setRealm(realm);
        authorizationHeader.setNonce(nonce);
        authorizationHeader.setURI(requestURI);
        authorizationHeader.setResponse(RESPONSE);
        authorizationHeader.setAlgorithm("MD5");
        if (qop != null) {
            authorizationHeader.setQop(qop);
            authorizationHeader.setCNonce(cNonce);
            authorizationHeader.setNonceCount(1);
        }
        registerRequest.addHeader(authorizationHeader);
        return registerRequest;
    }

    public Request createUnRegisterRequest(SipPlatform sipPlatform, SipDevice sipDevice, long CSeq, String fromTag, String viaTag, CallIdHeader callIdHeader) throws ParseException, PeerUnavailableException, InvalidArgumentException {
        SipDevice copyDevice = new SipDevice();
        BeanUtils.copyProperties(sipDevice, copyDevice);
        copyDevice.setExpires(0);
        return createRegisterRequest(sipPlatform, copyDevice, CSeq, fromTag, viaTag, callIdHeader);
    }

    public Request createUnRegisterRequest(SipPlatform sipPlatform, SipDevice sipDevice, String fromTag, String viaTag,
                                           WWWAuthenticateHeader www, CallIdHeader callIdHeader) throws ParseException, PeerUnavailableException, InvalidArgumentException {
        SipDevice copyDevice = new SipDevice();
        BeanUtils.copyProperties(sipDevice, copyDevice);
        copyDevice.setExpires(0);
        return createRegisterRequest(sipPlatform, copyDevice, fromTag, viaTag, www, callIdHeader);
    }

    public Request createMessageRequest(SipPlatform parentPlatform, SipDevice sipDevice, String content, String fromTag, CallIdHeader callIdHeader) throws PeerUnavailableException, ParseException, InvalidArgumentException {
        Request request = null;
        // sipuri
        SipURI requestURI = sipFactory.createAddressFactory().createSipURI(parentPlatform.getServerGBId(), parentPlatform.getServerIP() + ":" + parentPlatform.getServerPort());
        // via
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = sipFactory.createHeaderFactory().createViaHeader(parentPlatform.getServerGBId(), parentPlatform.getServerPort(),
                parentPlatform.getTransport(), null);
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);
        // from
        SipURI fromSipURI = sipFactory.createAddressFactory().createSipURI(sipDevice.getDeviceId(),
                sipDevice.getIp() + ":" + sipDevice.getPort());
        Address fromAddress = sipFactory.createAddressFactory().createAddress(fromSipURI);
        FromHeader fromHeader = sipFactory.createHeaderFactory().createFromHeader(fromAddress, fromTag);
        // to
        SipURI toSipURI = sipFactory.createAddressFactory().createSipURI(parentPlatform.getServerGBId(), parentPlatform.getServerGBDomain());
        Address toAddress = sipFactory.createAddressFactory().createAddress(toSipURI);
        ToHeader toHeader = sipFactory.createHeaderFactory().createToHeader(toAddress, null);
        // Forwards
        MaxForwardsHeader maxForwards = sipFactory.createHeaderFactory().createMaxForwardsHeader(70);
        // ceq
        CSeqHeader cSeqHeader = sipFactory.createHeaderFactory().createCSeqHeader(1L, Request.MESSAGE);
        MessageFactoryImpl messageFactory = (MessageFactoryImpl) sipFactory.createMessageFactory();
        // 设置编码， 防止中文乱码
        messageFactory.setDefaultContentEncodingCharset("gb2312");
        request = messageFactory.createRequest(requestURI, Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        List<String> agentParam = new ArrayList<>();
        agentParam.add("wydpp");
        UserAgentHeader userAgentHeader = sipFactory.createHeaderFactory().createUserAgentHeader(agentParam);
        request.addHeader(userAgentHeader);
        ContentTypeHeader contentTypeHeader = sipFactory.createHeaderFactory().createContentTypeHeader("APPLICATION", "MANSCDP+xml");
        request.setContent(content, contentTypeHeader);
        return request;
    }
}
