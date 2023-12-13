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
@TableName("web_dictionary")
@ApiModel(value="Dictionary对象", description="")
public class Dictionary implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键 ID")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "字典 key")
    private String dicKey;

    @ApiModelProperty(value = "字典 value")
    private String dicValue;

    private Date createTime;

    private Date updateTime;

    private String extra;


}
