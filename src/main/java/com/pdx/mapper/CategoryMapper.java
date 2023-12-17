package com.pdx.mapper;

import com.pdx.model.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
public interface CategoryMapper extends BaseMapper<Category> {

    String selectParentName(@Param("pid") String pid);
}
