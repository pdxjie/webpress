package com.pdx.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/15
 * @Description: 添加/编辑用户 Vo
 */
@Data
public class OperateUserVo {

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "微信标识")
    private String openId;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "0 男 1 女 2 未知")
    private Integer sex;
}
