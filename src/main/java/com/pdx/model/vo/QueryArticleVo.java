package com.pdx.model.vo;

import com.pdx.utils.PageParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/13
 * @Description:
 */
@Data
public class QueryArticleVo extends PageParams {

    @ApiModelProperty(value = "分类 ID")
    private String categoryId;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "0 技术分享 1 面试题")
    private Integer type;

    @ApiModelProperty(value = "是否推荐")
    private boolean isRecommend;
}
