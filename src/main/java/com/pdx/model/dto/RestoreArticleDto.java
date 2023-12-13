package com.pdx.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/12/13
 * @Description:
 */
@Data
public class RestoreArticleDto {

    private List<String> successIds;

    private List<String> errorIds;
}
