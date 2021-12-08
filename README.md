# gb28181-client

#### 介绍
gb28181协议客户端模拟系统

一个参考wvp-GB28181-pro项目实现的gb28181客户端，可以模拟国标设备与wvp-GB28181-pro进行通信。
方便在开发阶段进行gb28181协议测试。

目前已实现功能：

1.设备注册到wvp服务，发送心跳，设备注销；

2.设备目录查询（Query-Catalog）；

3.设备播放推流功能（使用ffmpeg读取本地视频文件，推送到zml服务）；

wvp-GB28181-pro项目github地址：https://github.com/648540858/wvp-GB28181-pro

ZLMediaKit流媒体服务器地址：https://github.com/ZLMediaKit/ZLMediaKit
