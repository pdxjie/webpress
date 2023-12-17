package com.pdx.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/13
 * @Description:
 */
@Data
public class ArticlePageDto {

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "文章标题")
    private String articleTitle;

    @ApiModelProperty(value = "文章描述")
    private String description;

    @ApiModelProperty(value = "文章封面")
    private String cover;

    @ApiModelProperty(value = "0 技术分享 1 面试题")
    private Integer type;

    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

    @ApiModelProperty(value = "浏览数")
    private Integer viewCount;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否推荐")
    private boolean isRecommend;

    @ApiModelProperty(value = "是否点赞")
    private boolean isLike;

    @ApiModelProperty(value = "分类名称")
    private String cateName;
}
