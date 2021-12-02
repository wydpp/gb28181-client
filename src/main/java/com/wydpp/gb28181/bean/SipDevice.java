package com.wydpp.gb28181.bean;


public class SipDevice {

	/**
	 * 设备Id
	 */
	private String deviceId;

	/**
	 * 设备名
	 */
	private String name;
	
	/**
	 * 生产厂商
	 */
	private String manufacturer;
	
	/**
	 * 型号
	 */
	private String model;
	
	/**
	 * 固件版本
	 */
	private String firmware;

	/**
	 * 传输协议
	 * UDP/TCP
	 */
	private String transport;

	/**
	 * 数据流传输模式
	 * UDP:udp传输
	 * TCP-ACTIVE：tcp主动模式
	 * TCP-PASSIVE：tcp被动模式
	 */
	private String streamMode;

	/**
	 * wan地址_ip
	 */
	private String  ip;

	/**
	 * wan地址_port
	 */
	private int port;

	/**
	 * wan地址
	 */
	private String  hostAddress;
	
	/**
	 * 在线
	 */
	private boolean online;


	/**
	 * 注册时间
	 */
	private Long registerTime;

	private String registerTimeStr;


	/**
	 * 心跳时间
	 */
	private Long keepaliveTime;

	private String keepaLiveTimeStr;

	/**
	 * 通道个数
	 */
	private int channelCount;

	/**
	 * 注册有效期
	 */
	private int expires;

	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 更新时间
	 */
	private String updateTime;

	/**
	 * 设备使用的媒体id, 默认为null
	 */
	private String mediaServerId;

	/**
	 * 首次注册
	 */
	private boolean firsRegister;

	/**
	 * 字符集, 支持 utf-8 与 gb2312
	 */
	private String charset ;

	/**
	 * 目录订阅周期，0为不订阅
	 */
	private int subscribeCycleForCatalog ;


	private boolean needRegister = true;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFirmware() {
		return firmware;
	}

	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getStreamMode() {
		return streamMode;
	}

	public void setStreamMode(String streamMode) {
		this.streamMode = streamMode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public boolean getOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public boolean isOnline() {
		return online;
	}

	public Long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Long registerTime) {
		this.registerTime = registerTime;
	}

	public String getRegisterTimeStr() {
		return registerTimeStr;
	}

	public void setRegisterTimeStr(String registerTimeStr) {
		this.registerTimeStr = registerTimeStr;
	}

	public Long getKeepaliveTime() {
		return keepaliveTime;
	}

	public void setKeepaliveTime(Long keepaliveTime) {
		this.keepaliveTime = keepaliveTime;
	}

	public String getKeepaLiveTimeStr() {
		return keepaLiveTimeStr;
	}

	public void setKeepaLiveTimeStr(String keepaLiveTimeStr) {
		this.keepaLiveTimeStr = keepaLiveTimeStr;
	}

	public int getExpires() {
		return expires;
	}

	public void setExpires(int expires) {
		this.expires = expires;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getMediaServerId() {
		return mediaServerId;
	}

	public void setMediaServerId(String mediaServerId) {
		this.mediaServerId = mediaServerId;
	}

	public boolean isFirsRegister() {
		return firsRegister;
	}

	public void setFirsRegister(boolean firsRegister) {
		this.firsRegister = firsRegister;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getSubscribeCycleForCatalog() {
		return subscribeCycleForCatalog;
	}

	public void setSubscribeCycleForCatalog(int subscribeCycleForCatalog) {
		this.subscribeCycleForCatalog = subscribeCycleForCatalog;
	}

	public boolean isNeedRegister() {
		return needRegister;
	}

	public void setNeedRegister(boolean needRegister) {
		this.needRegister = needRegister;
	}
}
