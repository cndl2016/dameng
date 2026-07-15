package com.dm.cn.common.utils.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * IO工具类
 *
 * @author dameng
 */
public class IoUtils {
    private static final Logger log = LoggerFactory.getLogger(IoUtils.class);
    /**
     * 关闭IO
     */
    public static void close(InputStreamReader inputStreamReader, BufferedReader bufferedReader) {
        try {
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            log.error("IoUtils close IOException {}",e);
        }
    }
}
