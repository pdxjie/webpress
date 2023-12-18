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

/**
 * <p>
 *
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Data
public class BannerDto implements Serializable {

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "轮播图封面")
    private String cover;

    @ApiModelProperty(value = "轮播图链接")
    private String url;

    @ApiModelProperty(value = "对应文章 ID")
    private String articleId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "关联文章名称")
    private String articleName;

    @ApiModelProperty(value = "文章类型")
    private Integer type;
}
