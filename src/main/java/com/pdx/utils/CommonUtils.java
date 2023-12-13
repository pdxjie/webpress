package com.pdx.utils;

import cn.hutool.core.lang.UUID;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/12
 * @Description: 通用工具类
 */
public class CommonUtils {

    /**
     * 获取随机 ID
     * @return ID
     */
    public static String uuid() {
        return UUID.randomUUID(true).toString();
    }

}
