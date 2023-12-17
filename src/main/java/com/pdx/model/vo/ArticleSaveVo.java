package com.pdx.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/13
 * @Description:
 */
@Data
public class ArticleSaveVo {

    @ApiModelProperty(value = "分类 ID")
    private String categoryId;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章描述")
    private String description;

    @ApiModelProperty(value = "文章封面")
    private String cover;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "0 技术分享 1 面试题")
    private Integer type;

    @ApiModelProperty(value = "是否推荐")
    private boolean isRecommend;
}
