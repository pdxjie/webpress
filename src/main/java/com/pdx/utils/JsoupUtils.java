package com.pdx.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/19
 * @Description:
 */
@Component
public class JsoupUtils {

    /**
     * 获取随机头像
     * @return 头像地址
     */
    public String getWxAvatar() {
        String avatarUrl = "";
        Random rand = new Random();
        int randomNum = rand.nextInt(4) + 1;
        String url = "https://www.qqtn.com/tx/weixintx_"+ randomNum +".html";
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 100000);
            Elements avatarUl = document.getElementsByClass("g-gxlist-imgbox");
            Elements avatarLi = avatarUl.select("li");
            int total = avatarLi.size();
            int index = rand.nextInt(total) + 1;
            Element li = avatarLi.get(index);
            avatarUrl = li.select("img").attr("src");
            System.out.println(avatarUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return avatarUrl;
    }
}
