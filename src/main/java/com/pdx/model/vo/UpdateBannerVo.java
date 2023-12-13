package com.pdx.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/12
 * @Description:
 */
@Data
public class UpdateBannerVo {

    @ApiModelProperty(value = "Banner ID")
    @NotBlank(message = "Banner ID 不能为空！")
    private String bannerId;

    @ApiModelProperty(value = "封面")
    private String cover;

    @ApiModelProperty(value = "关联文章 ID")
    private String articleId;
}
