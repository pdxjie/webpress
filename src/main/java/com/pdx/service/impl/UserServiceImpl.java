package com.pdx.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.javafaker.Faker;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.pdx.constants.RoleType;
import com.pdx.model.entity.Role;
import com.pdx.model.entity.User;
import com.pdx.model.entity.UserRole;
import com.pdx.exception.BusinessException;
import com.pdx.mapper.RoleMapper;
import com.pdx.mapper.UserMapper;
import com.pdx.mapper.UserRoleMapper;
import com.pdx.model.dto.UserDto;
import com.pdx.model.vo.*;
import com.pdx.response.ResponseCode;
import com.pdx.response.Result;
import com.pdx.service.UserService;
import com.pdx.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pdx.constants.BasicConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 派同学
 * @since 2023-07-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private VerificationCode verificationCode;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private OssUtils ossUtils;

    @Value("${spring.mail.username}")
    private String username;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private SecurityUtil securityUtil;

    @Resource
    private JsoupUtils jsoupUtils;

    @Override
    public List<String> currentRoles(String userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", userId));
        if (userRoles == null) {
            return new ArrayList<>();
        }
        // 拿到所有的角色 ID
        List<String> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        // 根据角色 ID 查询出所有的角色码
        List<String> roleCodes = roleMapper.selectBatchIds(roleIds).stream().map(Role::getRoleCode).collect(Collectors.toList());
        return roleCodes;
    }

    @Override
    public Result<?> login(LoginVo loginVo) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        User userOne = getOne(wrapper.eq("email", loginVo.getEmail()));
        if (null == userOne) {
            throw new BusinessException(ResponseCode.USERINFO_IS_NOT_EXISTS);
        }
        UserRole userRole = userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("user_id", userOne.getId()));
        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("id", userRole.getRoleId()));
        // 判断用户是否是管理员
        if (!Objects.equals(role.getRoleCode(), RoleType.ADMIN)) {
            throw new BusinessException(ResponseCode.USER_ROLE_IS_MUST_BE_ADMIN);
        }

        // 判断用户是否被禁用
        if (userOne.isStatus()) {
            throw new BusinessException(ResponseCode.USER_FORBIDDEN);
        }

        // 校验密码
        String salt = userOne.getSalt();
        if (!PasswordUtils.matches(salt, loginVo.getPassword(), userOne.getPassword())) {
            throw new BusinessException(ResponseCode.USERINFO_IS_NOT_EXISTS);
        }
        // 更新用户状态
        User user = User.builder().isOnline(true).build();
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userOne.getId());
        update(user, updateWrapper);
        Map<String, Object> resultMap = resultMap(userOne.getId());
        return Result.success(resultMap);
    }

    @Override
    public Result<?> registerUser(RegisterVo registerVo) {
        // 判断该邮箱是否已经存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        User userOne = getOne(wrapper.eq("email", registerVo.getEmail()));
        if (null != userOne) {
            throw new BusinessException(ResponseCode.USER_INFO_EXISTED);
        }
        // 校验密码与确认密码是否相等
        if (!registerVo.getPassword().equals(registerVo.getConfirmPassword())) {
            throw new BusinessException(ResponseCode.PASSWORD_IS_NOT_EQUAL);
        }
        // 校验验证码是否（存在）过期
        Boolean isExisted = redisTemplate.hasKey(CAPTCHA_PREFIX + registerVo.getEmail());
        if (!isExisted) {
            throw new BusinessException(ResponseCode.INVALID_CODE);
        }
        // 获取验证码
        String code = (String) redisTemplate.opsForValue().get(CAPTCHA_PREFIX + registerVo.getEmail());
        // 比较验证码
        if (!code.equalsIgnoreCase(registerVo.getCode())) {
            throw new BusinessException(ResponseCode.ERROR_CODE);
        }

        // 删除Redis验证码
        redisTemplate.delete(CAPTCHA_PREFIX + registerVo.getEmail());

        // 判断验证码是否输入
        if (StringUtils.isEmpty(registerVo.getCode())) {
            throw new BusinessException(ResponseCode.INVALID_CODE);
        }
        // 加密盐
        String salt = PasswordUtils.getSalt();
        // 加密密码
        String encodePass = PasswordUtils.encode(registerVo.getPassword(), salt);
        String userId = UUID.randomUUID().toString();
        User user = User.builder()
                        .id(userId)
                        .email(registerVo.getEmail())
                        .salt(salt)
                        .avatar(DEFAULT_AVATAR)
                        .nickName("新用户 — " + userId.substring(0,5))
                        .remark("说些什么介绍一下自己吧~")
                        .password(encodePass)
                        .isOnline(true)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
        boolean isSuccess = save(user);
        // 初次登录 用户角色是普通用户 USER
        String userRoleId = UUID.randomUUID().toString();
        UserRole userRole = UserRole.builder().roleId(RoleType.USER_KEY).userId(userId).id(userRoleId).createTime(new Date()).updateTime(new Date()).build();
        int result = userRoleMapper.insert(userRole);
        if (isSuccess && result > 0) {
            // 获取用户角色 && 权限
            Map<String, Object> resultMap = resultMap(userId);
            return Result.success(resultMap);
        }
        return Result.fail(ResponseCode.FAIL);
    }

    @Override
    public Result<?> sendCode(String email) {
        String cacheKey = CAPTCHA_PREFIX + email;
        // 判断是否多次发送验证码
        Boolean isExist = redisTemplate.hasKey(cacheKey);
        // 如果存在 删除重新发送
        if (isExist) {
            redisTemplate.delete(cacheKey);
        }
        // 生成验证码code
        String code = verificationCode.VerificationCode();
        // 将验证码存放到Redis中 设置过期时间 使用Set结构防止用户多次发送验证码导致存储太多相同key的值
        redisTemplate.opsForValue().set(cacheKey, code, 5, TimeUnit.MINUTES);
        // 创建邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(username);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("平台用户注册验证码邮件");
            // 将验证码转换为数组
            mimeMessage.setContent(verificationCode.buildContent( String.valueOf(code.charAt(0)),
                    String.valueOf(code.charAt(1)), String.valueOf(code.charAt(2)), String.valueOf(code.charAt(3))), "text/html;charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);
        return Result.success();
    }

    /**
     * 根据条件检索用户列表
     * @param userVo
     * @return
     */
    @Override
    public Result<?> queryByCondition(UserVo userVo) {
        // 拼接检索条件
        Page<User> page = new Page<>(userVo.getCurrent(), userVo.getPageSize());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(userVo.getEmail())) {
            wrapper.like("email", userVo.getEmail());
        }
        if (StringUtils.isNotEmpty(userVo.getNickName())) {
            wrapper.like("nick_name", userVo.getNickName());
        }
        if (StringUtils.isNotEmpty(userVo.getStartTime())) {
            wrapper.ge("create_time", userVo.getStartTime());
        }
        if (StringUtils.isNotEmpty(userVo.getEndTime())) {
            wrapper.le("create_time", userVo.getEndTime());
        }
        wrapper.eq("is_deleted", 0);
        Page<User> userPage = page(page, wrapper);
        Map<String, Object> resultMap = new HashMap<>();
        List<User> records = userPage.getRecords();
        List<UserDto> userDtos = new ArrayList<>();
        records.stream().forEach(user -> {
            UserDto userDto = new UserDto();
            UserRole userRole = userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("user_id", user.getId()));
            Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("id", userRole.getRoleId()));
            BeanUtils.copyProperties(user, userDto);
            userDto.setRole(role.getRoleName());
            userDtos.add(userDto);
        });
        resultMap.put("users", userDtos);
        resultMap.put("total", userPage.getTotal());
        return Result.success(resultMap);
    }

    /**
     * 新增用户
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> insertUser(OperateUserVo vo) {
        if (StringUtils.isEmpty(vo.getPassword())) {
            throw new BusinessException(ResponseCode.ERROR_PARAM);
        }
        String openId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String salt = PasswordUtils.getSalt();
        String encode = PasswordUtils.encode(vo.getPassword(), salt);
        User user = User.builder()
                .id(userId)
                .openId(openId)
                .avatar(DEFAULT_AVATAR)
                .nickName(vo.getNickName())
                .salt(salt)
                .email(vo.getEmail())
                .remark(StrUtil.isBlank(vo.getRemark()) ? DEFAULT_REMARK : vo.getRemark())
                .password(encode)
                .sex(vo.getSex())
                .status(false)
                .createTime(new Date())
                .updateTime(new Date())
                .address("未知")
                .isDeleted(0)
                .build();
        String userRoleId = UUID.randomUUID().toString();
        UserRole userRole = UserRole.builder().roleId(RoleType.USER_KEY).userId(userId).id(userRoleId).createTime(new Date()).updateTime(new Date()).build();
        int index = userRoleMapper.insert(userRole);
        boolean result = save(user);
        return result && index > 0 ? Result.success() : Result.fail();
    }

    /**
     * 根据ID获取用户信息
     * @param id
     * @return
     */
    @Override
    public Result<?> getOneById(String id) {
        User user = getById(id);
        // 将密码重置为空 返回前端
        user.setPassword("");
        return Result.success(user);
    }

    /**
     * 修改用户信息
     * @param vo
     * @return
     */
    @Override
    public Result<?> updateInfo(OperateUserVo vo) {
        if (StrUtil.isBlank(vo.getId())) {
            throw new BusinessException(ResponseCode.ERROR_PARAM);
        }
        String encode = "";
        User userInfo = baseMapper.selectOne(new QueryWrapper<User>().eq("id", vo.getId()));
        if (StrUtil.isNotBlank(vo.getPassword())) {
            encode = PasswordUtils.encode(vo.getPassword(), userInfo.getSalt());
        } else {
            encode = userInfo.getPassword();
        }
        // 封装参数
        User user = User.builder()
                .nickName(vo.getNickName())
                .email(vo.getEmail())
                .remark(vo.getRemark())
                .password(encode)
                .updateTime(new Date())
                .sex(vo.getSex())
                .build();
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", vo.getId());
        boolean result = update(user, wrapper);
        return result ? Result.success() : Result.fail();
    }

    /**
     * 修改用户头像
     * @param id
     * @param file
     * @return
     */
    @Override
    public Result<?> updateAvatar(String id, MultipartFile file) {
        User user = getById(id);
        String url = ossUtils.uploadFileAvatar(file);
        user.setAvatar(url);
        user.setUpdateTime(new Date());
        boolean result = updateById(user);
        return result ? Result.success() : Result.fail();
    }

    /**
     * 修改用户密码
     * @param vo
     * @return
     */
    @Override
    public Result<?> updatePassword(UpdatePasswordVo vo) {
        User user = getById(vo.getId());
        String salt = user.getSalt();
        // 校验旧密码是否正确
        if (!PasswordUtils.matches(salt, vo.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.PASSWORD_IS_NOT_EQUAL);
        }
        // 修改新密码
        String encode = PasswordUtils.encode(vo.getNewPassword(), salt);
        user.setPassword(encode);
        user.setUpdateTime(new Date());
        boolean result = updateById(user);
        return result ? Result.success() : Result.fail();
    }

    /**
     * 删除用户信息
     * 逻辑删除
     * @param id
     * @return
     */
    @Override
    public Result<?> removeUserById(String id) {
        User user = getById(id);
        user.setIsDeleted(1);
        user.setUpdateTime(new Date());
        boolean result = updateById(user);
        return result ? Result.success() : Result.fail();
    }

    /**
     * 封禁用户账号
     * @param id
     * @return
     */
    @Override
    public Result<?> forbiddeUser(String id) {
        User user = getById(id);
        user.setStatus(!user.isStatus());
        user.setUpdateTime(new Date());
        boolean result = updateById(user);
        return result ? Result.success() : Result.fail();
    }

    /**
     * 退出功能
     * @param id
     * @return
     */
    @Override
    public Result<?> logout(String id) {
        User user = getById(id);
        user.setOnline(false);
        user.setUpdateTime(new Date());
        boolean result = updateById(user);
        return result ? Result.success() : Result.fail();
    }

    /**
     * 设置角色
     * @param userId
     * @param roleId
     * @return
     */
    @Override
    public Result<?> setRole(String userId, String roleId) {
        UserRole userRole = UserRole.builder().id(UUID.randomUUID().toString()).roleId(roleId).userId(userId).createTime(new Date()).updateTime(new Date()).build();
        int index = userRoleMapper.insert(userRole);
        return index > 0 ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> getUserInfo() {
        String accessToken = securityUtil.getPrincipal();
        String address = securityUtil.getAddress();
        if (DEFAULT_ADDRESS.equals(address)) {
            address = "未知";
        }
        String userId = jwtUtil.getUserIdByJwtToken(accessToken);
        User userOne = getById(userId);
        CurrentUser userInfo = CurrentUser.builder().userId(userOne.getId()).avatar(userOne.getAvatar()).nickName(userOne.getNickName()).isOnline(true).address(address).lastLoginTime(new Date()).build();
        Map<String, Object> resultMap = resultMap(userOne.getId());
        resultMap.put("userInfo", userInfo);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> batchUpload(MultipartFile file) {
        String url = ossUtils.uploadFileAvatar(file);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("url", url);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> wxLogin(String openId) {
        // 判断用户是否存在
        User userOne = baseMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        // 如果存在 则执行登录的操作
        if (null != userOne) {
            // 判断用户是否被禁用
            if (userOne.isStatus()) {
                throw new BusinessException(ResponseCode.USER_FORBIDDEN);
            }
            // 更新用户状态
            User user = User.builder().isOnline(true).build();
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", userOne.getId());
            update(user, updateWrapper);
            Map<String, Object> resultMap = resultMap(userOne.getId());
            CurrentUser userInfo = CurrentUser.builder().userId(userOne.getId()).avatar(userOne.getAvatar()).nickName(userOne.getNickName()).isOnline(true).lastLoginTime(new Date()).build();
            resultMap.put("userInfo", userInfo);
            return Result.success(resultMap);
        }
        // 如果不存在 则执行注册再登录的操作
        Faker faker = new Faker();
        String nickName = faker.name().fullName();
        String avatar = "";
        String userId = CommonUtils.uuid();
        try {
            avatar = jsoupUtils.getWxAvatar();
        } catch (Exception e) {
            avatar = DEFAULT_AVATAR;
        }
        String salt = PasswordUtils.getSalt();
        String encode = PasswordUtils.encode(DEFAULT_PASSWORD, salt);
        User user = User.builder()
                        .id(userId).remark(DEFAULT_REMARK).email(DEFAULT_EMAIL)
                        .nickName(nickName).openId(openId).avatar(avatar).password(encode)
                        .salt(salt).isOnline(true).isDeleted(0).status(false).sex(2)
                        .updateTime(new Date()).createTime(new Date()).build();
        int result = baseMapper.insert(user);
        if (result > 0) {
            // 初次登录 用户角色是普通用户 USER
            String userRoleId = UUID.randomUUID().toString();
            UserRole userRole = UserRole.builder().roleId(RoleType.USER_KEY).userId(userId).id(userRoleId).createTime(new Date()).updateTime(new Date()).build();
            userRoleMapper.insert(userRole);
            // 获取用户角色 && 权限
            Map<String, Object> resultMap = resultMap(userId);
            CurrentUser userInfo = CurrentUser.builder().userId(user.getId()).avatar(user.getAvatar()).nickName(user.getNickName()).isOnline(true).lastLoginTime(new Date()).build();
            resultMap.put("userInfo", userInfo);
            return Result.success(resultMap);
        }
        return Result.fail();
    }

    @Override
    public Result<?> updateNickName(UpdateNickNameVo vo) {
        User user = User.builder().nickName(vo.getNickName()).build();
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("open_id", vo.getOpenId());
        int result = baseMapper.update(user, updateWrapper);
        User selectOne = baseMapper.selectOne(new QueryWrapper<User>().eq("open_id", vo.getOpenId()));
        return Objects.equals(result, OPERATE_RESULT) ? Result.success(selectOne) : Result.fail()                                            ;
    }

    /**
     * 获取用户角色 && 权限 以及 Token信息
     * @param userId
     * @return
     */
    private Map<String, Object> resultMap(String userId) {
        String accessToken = jwtUtil.generateToken(userId);
        // 获取用户角色
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", accessToken);
        resultMap.put("roles", currentRoles(userId));
        return resultMap;
    }
}
