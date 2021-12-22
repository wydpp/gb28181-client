/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wydpp.cache;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Disk cache.
 *
 * @author xuanyin
 */
public class DiskCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskCache.class);

    /**
     * Write service info to dir.
     *
     * @param dir directory
     */
    public static void write(JSONObject jsonObject, String dir) {

        try {
            makeSureCacheDirExists(dir);

            File file = new File(dir);
            if (!file.exists()) {
                // add another !file.exists() to avoid conflicted creating-new-file from multi-instances
                if (!file.createNewFile() && !file.exists()) {
                    throw new IllegalStateException("failed to create cache file");
                }
            }
            StringBuilder keyContentBuffer = new StringBuilder();
            keyContentBuffer.append(jsonObject.toJSONString());

            //Use the concurrent API to ensure the consistency.
            ConcurrentDiskUtil.writeFileContent(file, keyContentBuffer.toString(), Charset.defaultCharset().toString());

        } catch (Throwable e) {
            LOGGER.error("[NA] failed to write cache:" + dir, e);
        }
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * Read service info from disk.
     *
     * @param cacheDir cache file dir
     * @return service infos
     */
    public static Map<String, JSONObject> read(String cacheDir) {
        Map<String, JSONObject> domMap = new HashMap<String, JSONObject>(16);

        BufferedReader reader = null;
        try {
            File[] files = makeSureCacheDirExists(cacheDir).listFiles();
            if (files == null || files.length == 0) {
                return domMap;
            }

            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }

                String fileName = URLDecoder.decode(file.getName(), "UTF-8");
                try {
                    String dataString = ConcurrentDiskUtil
                            .getFileContent(file, Charset.defaultCharset().toString());
                    reader = new BufferedReader(new StringReader(dataString));

                    String json;
                    while ((json = reader.readLine()) != null) {
                        try {
                            if (!json.startsWith("{")) {
                                continue;
                            }
                        } catch (Throwable e) {
                            LOGGER.error("[NA] error while parsing cache file: " + json, e);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("[NA] failed to read cache for dom: " + file.getName(), e);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error("[NA] failed to read cache file", e);
        }
        return domMap;
    }

    private static File makeSureCacheDirExists(String dir) {
        File cacheDir = new File(dir);

        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs() && !cacheDir.exists()) {
                throw new IllegalStateException("failed to create cache dir: " + dir);
            }
        }
        return cacheDir;
    }
}
