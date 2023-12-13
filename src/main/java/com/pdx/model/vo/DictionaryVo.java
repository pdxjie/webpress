package com.pdx.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Data
public class DictionaryVo implements Serializable {

    @ApiModelProperty(value = "字典 ID")
    private String id;

    @ApiModelProperty(value = "字典 key")
    private String dicKey;

    @ApiModelProperty(value = "字典 value")
    private String dicValue;


}
