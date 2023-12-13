package com.pdx.aspect;

import com.pdx.annotation.CurrentLimit;
import com.pdx.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.validator.constraints.Currency;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/12
 * @Description:
 */
@Aspect
@Order(1)
@Component
public class AssessLimitAspect {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.pdx.annotation.CurrentLimit)")
    public void limitPointCut () {}

    @Around("limitPointCut()")
    public Object around (ProceedingJoinPoint point) throws Throwable {

        // 获取被注解的方法
        MethodInvocationProceedingJoinPoint joinPoint = (MethodInvocationProceedingJoinPoint) point;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的注解
        CurrentLimit annotation = (CurrentLimit) method.getAnnotation(Currency.class);
        if (annotation == null) {
            // 如果方法上没有注解，则继续调用，不做限流处理
            return point.proceed();
        }

        /**
         * 代码执行到这里，说明有 CurrentLimit 注解，那么就需要做限流处理
         * 1. 可以使用 Redis 的 incr 命令，每次请求都对 key 做自增操作，如果自增后的值大于注解中的 count 值，则说明请求次数超过了限制
         * 2. 可以使用 Redis 的 zset 数据结构，每次请求都将当前时间戳作为 score，请求的 url 作为 member，
         *    然后使用 zremrangeByScore 命令删除指定时间范围内的数据，最后使用 zcard 命令获取当前 zset 的长度，如果长度大于注解中的 count 值，则说明请求次数超过了限制
         */
        // 获取 request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 获取请求 IP 地址
        String ip = getIpAddr(request);
        // 请求 url 路径
        String uri = request.getRequestURI();
        //存到 redis 中的 key
        String key = "CurrentLimit:" + ip + ":" + uri;
        // 缓存中存在 key，在限定访问周期内已经调用过当前接口
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            // 访问次数自增1
            redisTemplate.opsForValue().increment(key, 1);
            // 超出访问次数限制
            Integer count = (Integer) redisTemplate.opsForValue().get(key);
            // 如果 count 为 null，说明缓存中的数据已经过期，需要重新设置
            if (null == count) {
                count = 0;
            }
            if (count > annotation.count()) {
                throw new BusinessException(annotation.message());
            }
            // 未超出访问次数限制，不进行任何操作，返回true
        } else {
            // 第一次设置数据,过期时间为注解确定的访问周期
            redisTemplate.opsForValue().set(key, 1, annotation.cycle(), TimeUnit.SECONDS);
        }
        return point.proceed();
    }


    //获取请求的归属 IP 地址
    private String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个 IP 为客户端真实IP,多个 IP 按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}
