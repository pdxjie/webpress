package com.pdx.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("web_banner")
@ApiModel(value="Banner对象", description="")
public class Banner implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键 ID")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "轮播图封面")
    private String cover;

    @ApiModelProperty(value = "轮播图链接")
    private String url;

    @ApiModelProperty(value = "对应文章 ID")
    private String articleId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "扩展字段")
    private String extra;


}
