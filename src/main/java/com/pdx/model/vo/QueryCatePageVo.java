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
public class QueryCatePageVo extends PageParams {

    @ApiModelProperty(value = "分类名称")
    private String cateName;
}
