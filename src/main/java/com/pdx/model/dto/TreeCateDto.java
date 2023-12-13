package com.pdx.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Data
public class TreeCateDto implements Serializable {

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "父 ID")
    private String pid;

    @ApiModelProperty(value = "Icon 图标")
    private String icon;

    @ApiModelProperty(value = "子集")
    private List<TreeCateDto> children;

    @ApiModelProperty(value = "文章数")
    private int count;


}
