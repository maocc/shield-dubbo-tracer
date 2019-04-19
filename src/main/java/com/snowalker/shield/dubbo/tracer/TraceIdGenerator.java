package com.snowalker.shield.dubbo.tracer;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/2/20 13:53
 * @className TraceIdGenerator
 * @desc TraceId生成器
 */
public class TraceIdGenerator {

    private static final String[] CHARS = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
            "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"

    };
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceIdGenerator.class);

    /**
     * 消费端创建TraceId,并设置到线程上下文中 该方法只调用一次
     * 
     * @return
     */
    public static String createTraceId() {
        // 创建的同时就设置到上下文中
        String traceId = getTraceId();
        TraceIdUtil.setTraceId(traceId);
        return traceId;
    }

    public static String getTraceId() {
        try {
            return genTraceId();
        } catch (Exception e) {
            LOGGER.error("getTraceId error ", e);
            return "";
        }
    }

    private static String genTraceId() {
        String curTime = Long.toString(System.nanoTime());
        String[] aResult = encode(curTime);
        Random random = new Random();
        return aResult[random.nextInt(aResult.length)];
    }

    private static String[] encode(String url) {
        String key = "feezu";
        String hex = MD5.sign(key + url);
        String[] resUrl = new String[4];
        for (int i = 0; i < 4; i++) {
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            String outChars = "";
            for (int j = 0; j < 6; j++) {
                long index = 0x0000003D & lHexLong;
                outChars += CHARS[(int) index];
                lHexLong = lHexLong >> 5;
            }
            resUrl[i] = outChars;
        }
        return resUrl;
    }
}
