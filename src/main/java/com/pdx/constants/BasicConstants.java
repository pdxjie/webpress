package com.pdx.constants;

/*
 * @Author 派同学
 * @Description 基础常量
 * @Date 2023/7/24
 **/
public interface BasicConstants {

    // 基础扫描路径
    public static final String BASIC_SCAN_PATH = "com.pdx";

    public static final String TOKEN = "token";

    // 邮箱前缀 key
    public static final String CAPTCHA_PREFIX = "email_code";

    // 默认头像
    public static final String DEFAULT_AVATAR = "https://gd-hbimg.huaban.com/f745f4d45175d04e4c85f8c9f34409b1dd5eeb5612e43-VRcM5M_fw658webp";

    // 用户ID
    public static final String USER_ID_KEY = "userId";

    // 生产token时间
    public static final String CLAIM_DATE_KEY = "claim_date_key";

    // 默认简介
    public static final String DEFAULT_REMARK = "说些什么介绍一下自己吧~";

    // 默认群聊房间
    public static final String ROOM_ID = "roomId";

    // 聊天室 标识KEY
    public static final String ROOM_KEY = "chat-room::";

    // 聊天室 用户KEY
    public static final String ROOM_USERS = "room::users";

    // 内网IP
    public static final String DEFAULT_ADDRESS = "内网IP";

    // 聊天记录标识
    public static final String CHAT_KEY = "chat_key::";

    // 八股标识
    public static final String INTERVIEW_QUESTION_KEY = "interview_question_key::";

    // 操作结果
    public static final int OPERATE_RESULT = 1;

    // 下一个
    public static final String NEXT_ONE = "next";

    // 上一个
    public static final String PREVIOUS_ONE = "previous";

    // 父级分类的 ID
    public static final String PARENT_CATE = "0";
}
