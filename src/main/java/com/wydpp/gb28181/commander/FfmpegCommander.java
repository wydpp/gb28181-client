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
public class FfmpegCommander {

    private final Logger logger = LoggerFactory.getLogger(FfmpegCommander.class);

    @Autowired
    private SystemConfig systemConfig;

    private static final Map<String, Process> processMap = new ConcurrentHashMap<>();

    private static final Map<Integer, String> portCallIdMap = new ConcurrentHashMap<>();

    public String pushVideoStream(String callId, String ip, int port) {
        String command = systemConfig.getFfmpegPath() + " " +
                systemConfig.getFfmpegPushStreamCmd().replace("{ip}", ip).replace("{port}", port + "");
        logger.info("callId={},推流命令={}", callId, command);
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            processMap.put(callId, process);
            portCallIdMap.put(port, callId);
            InputStream errorInputStream = process.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(errorInputStream));
            StringBuffer errorStr = new StringBuffer();
            String str;
            int times = 0;
            while ((str = reader.readLine()) != null) {
                times++;
                if (times >= 400) {
                    break;
                }
                errorStr.append(str);
                System.out.println(str);
            }
            System.out.println(errorStr);
            new Thread(() -> {
                int code = 0;
                try {
                    code = process.waitFor();
                    System.out.println("code=" + code);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(code);
            }).start();
            return command;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void stopPushStream(String callId) {
        if (StringUtils.isEmpty(callId)){
            stopAllPushStream();
        }else {
            processMap.get(callId).destroy();
        }
    }

    public void stopAllPushStream() {
        processMap.entrySet().stream().forEach(entry -> {
            entry.getValue().destroy();
        });
    }
}
