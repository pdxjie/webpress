package com.pdx.utils;

import com.pdx.constants.BasicConstants;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/*
 * @Author 派同学
 * @Description 获取个人凭证信息
 * @Date 2023/8/9
 **/
@Component
public class SecurityUtil {

    @Resource
    private Ip2regionSearcher ip2regionSearcher;

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 获取个人凭证信息
     * @return Token
     */
    public String getPrincipal () {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader(BasicConstants.TOKEN);
    }

    /**
     * 获取当前用户 ID
     * @return 当前用户 ID
     */
    public String getCurrentUserId() {
        String accessToken = getPrincipal();
        return jwtUtil.getUserIdByJwtToken(accessToken);
    }

    /**
     * 获取用户所在地
     * @return 用户所在地
     */
    public String getAddress () {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String ipAddress = IPUtil.getIpAddress(request);
        return ip2regionSearcher.getAddress(ipAddress);
    }
}
