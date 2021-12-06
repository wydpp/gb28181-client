package com.wydpp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfig {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${ffmpeg.pushStreamCmd}")
    private String ffmpegPushStreamCmd;

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getFfmpegPushStreamCmd() {
        return ffmpegPushStreamCmd;
    }

    public void setFfmpegPushStreamCmd(String ffmpegPushStreamCmd) {
        this.ffmpegPushStreamCmd = ffmpegPushStreamCmd;
    }
}
