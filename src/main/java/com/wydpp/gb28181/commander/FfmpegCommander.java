package com.wydpp.gb28181.commander;

import com.wydpp.config.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FfmpegCommander implements IFfmpegCommander {

    private final Logger logger = LoggerFactory.getLogger(FfmpegCommander.class);

    @Autowired
    private SystemConfig systemConfig;

    private static final Map<String, Process> processMap = new ConcurrentHashMap<>();

    public String pushStream(String callId, String filePath, String ip, int port) {
        String command = systemConfig.getFfmpegPath() + " " +
                systemConfig.getFfmpegPushStreamCmd().replace("{filePath}", filePath).replace("{ip}", ip).replace("{port}", port + "");
        logger.info("callId={},\r\n推流命令={}", callId, command);
        Runtime runtime = Runtime.getRuntime();
        try {
            new Thread(() -> {
                int code = 0;
                try {
                    Process process = runtime.exec(command);
                    processMap.put(callId, process);
                    InputStream errorInputStream = process.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(errorInputStream));
                    StringBuffer errorStr = new StringBuffer();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        errorStr.append(str);
                        logger.debug(str);
                    }
                    code = process.waitFor();
                    logger.info("推流已结束,callId={}", callId);
                } catch (Exception e) {
                    logger.error("ffmpeg推流异常!", e);
                }
                System.out.println(code);
            }).start();
            return command;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void closeStream(String callId) {
        logger.info("关闭推流:{}", callId);
        if (StringUtils.isEmpty(callId)) {
            closeAllStream();
        } else if (processMap.containsKey(callId)) {
            processMap.get(callId).destroy();
        } else {
            logger.info("没有推流要关闭!");
        }
    }

    public void closeAllStream() {
        logger.info("关闭所有推流");
        processMap.entrySet().stream().forEach(entry -> {
            entry.getValue().destroy();
        });
    }
}
