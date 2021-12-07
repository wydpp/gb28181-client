package com.wydpp.gb28181.processor.request.impl;

import com.wydpp.gb28181.bean.SendRtpItem;
import com.wydpp.gb28181.bean.SipDevice;
import com.wydpp.gb28181.bean.SipPlatform;
import com.wydpp.gb28181.commander.FfmpegCommander;
import com.wydpp.gb28181.commander.SIPCommander;
import com.wydpp.gb28181.event.SipSubscribe;
import com.wydpp.gb28181.processor.SIPProcessorObserver;
import com.wydpp.gb28181.processor.request.ISIPRequestProcessor;
import com.wydpp.gb28181.processor.request.SIPRequestProcessorParent;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sdp.*;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Vector;

/**
 * SIP命令类型： INVITE请求
 */
@SuppressWarnings("rawtypes")
@Component
public class InviteRequestProcessor extends SIPRequestProcessorParent implements InitializingBean, ISIPRequestProcessor {

    private final static Logger logger = LoggerFactory.getLogger(InviteRequestProcessor.class);

    private String method = "INVITE";

    @Autowired
    private SIPCommander sipCommander;

    @Autowired
    private SIPProcessorObserver sipProcessorObserver;

    @Autowired
    private SipDevice sipDevice;

    @Autowired
    private SipPlatform sipPlatform;

    @Autowired
    private FfmpegCommander ffmpegCommander;

    @Autowired
    private SipSubscribe sipSubscribe;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加消息处理的订阅
        sipProcessorObserver.addRequestProcessor(method, this);
    }

    /**
     * 处理invite请求
     *
     * @param evt 请求消息
     */
    @Override
    public void process(RequestEvent evt) {
        //Invite Request消息实现，请求视频指令
        try {
            Request request = evt.getRequest();
            SipURI sipURI = (SipURI) request.getRequestURI();
            String channelId = sipURI.getUser();
            String requesterId = null;
            FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
            AddressImpl address = (AddressImpl) fromHeader.getAddress();
            SipUri uri = (SipUri) address.getURI();
            requesterId = uri.getUser();
            if (requesterId == null || channelId == null) {
                logger.info("无法从FromHeader的Address中获取到平台id，返回400");
                responseAck(evt, Response.BAD_REQUEST); // 参数不全， 发400，请求错误
                return;
            }
            // 非上级平台请求，查询是否设备请求（通常为接收语音广播的设备）
            logger.info("收到设备" + requesterId + "的语音广播Invite请求");
            //responseAck(evt, Response.TRYING);
            String contentString = new String(request.getRawContent());
            // jainSip不支持y=字段， 移除移除以解析。
            String substring = contentString;
            String ssrc = "0000000404";
            int ssrcIndex = contentString.indexOf("y=");
            if (ssrcIndex > 0) {
                substring = contentString.substring(0, ssrcIndex);
                ssrc = contentString.substring(ssrcIndex + 2, ssrcIndex + 12);
            }
            ssrcIndex = substring.indexOf("f=");
            if (ssrcIndex > 0) {
                substring = contentString.substring(0, ssrcIndex);
            }
            SessionDescription sdp = SdpFactory.getInstance().createSessionDescription(substring);
            //  获取支持的格式
            Vector mediaDescriptions = sdp.getMediaDescriptions(true);
            int port = -1;
            for (int i = 0; i < mediaDescriptions.size(); i++) {
                MediaDescription mediaDescription = (MediaDescription) mediaDescriptions.get(i);
                Media media = mediaDescription.getMedia();
                Vector mediaFormats = media.getMediaFormats(false);
                if (mediaFormats.contains("98")) {
                    port = media.getMediaPort();
                    break;
                }
            }
            if (port == -1) {
                logger.info("不支持的媒体格式，返回415");
                // 回复不支持的格式
                responseAck(evt, Response.UNSUPPORTED_MEDIA_TYPE); // 不支持的格式，发415
                return;
            }
            String username = sdp.getOrigin().getUsername();
            String addressStr = sdp.getOrigin().getAddress();
            logger.info("设备{}请求语音流，地址：{}:{}，ssrc：{}", username, addressStr, port, ssrc);
            SendRtpItem sendRtpItem = new SendRtpItem();
            sendRtpItem.setIp(addressStr);
            sendRtpItem.setPort(port);
            CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
            sipSubscribe.addOkSubscribe(callIdHeader.getCallId(), new SipSubscribe.Event() {
                @Override
                public void response(SipSubscribe.EventResult eventResult) {
                    logger.info("开始推流");
                    ffmpegCommander.stopAllPushStream();
                    ffmpegCommander.pushVideoStream(eventResult.callId, sendRtpItem.getIp(), sendRtpItem.getPort());
                }
            });
            StringBuffer content = new StringBuffer(200);
            content.append("v=0\r\n");
            content.append("o=" + channelId + " 0 0 IN IP4 " + addressStr + "\r\n");
            content.append("s=Play\r\n");
            content.append("c=IN IP4 " + addressStr + "\r\n");
            content.append("t=0 0\r\n");
            content.append("m=video " + sendRtpItem.getPort() + " RTP/AVP 96\r\n");
            content.append("a=sendonly\r\n");
            content.append("a=rtpmap:96 PS/90000\r\n");
            content.append("y=" + ssrc + "\r\n");
            content.append("f=\r\n");
            responseAck(evt, content.toString());
        } catch (SipException | InvalidArgumentException |
                ParseException e) {
            e.printStackTrace();
            logger.warn("sdp解析错误");
            e.printStackTrace();
        } catch (SdpParseException e) {
            e.printStackTrace();
        } catch (SdpException e) {
            e.printStackTrace();
        }
    }
}
