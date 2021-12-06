package com.wydpp.gb28181.commander;

import com.wydpp.config.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FfmpegCommander {

    @Autowired
    private SystemConfig systemConfig;

    private static final Map<String,Process> processMap = new ConcurrentHashMap<>();

    public String pushVideoStream(String callId,String ip, int port) {
        String command = systemConfig.getFfmpegPath() + " " +
                systemConfig.getFfmpegPushStreamCmd().replace("{ip}", ip).replace("{port}", port + "");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            processMap.put(callId,process);
            InputStream errorInputStream = process.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(errorInputStream));
            StringBuffer errorStr = new StringBuffer();
            String str;
            while((str = reader.readLine()) != null){
                errorStr.append(str);
                System.out.println(str);
            }
            System.out.println(errorStr);
            new Thread(()->{
                int code = 0;
                try {
                    code = process.waitFor();
                    System.out.println("code="+code);
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

    public void stopPushStream(String callId){
        processMap.get(callId).destroy();
    }
}
