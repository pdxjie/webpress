package com.pdx.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/13
 * @Description:
 */
@Data
public class CategoryVo {

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "父 ID")
    private String pid;

    @ApiModelProperty(value = "Icon 图标")
    private String icon;
}
