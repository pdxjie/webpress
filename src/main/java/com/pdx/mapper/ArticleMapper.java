package com.pdx.mapper;

import com.pdx.model.dto.ArticlePageDto;
import com.pdx.model.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdx.model.vo.QueryArticleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
public interface ArticleMapper extends BaseMapper<Article> {

    void increViewCount(@Param("id") String id);

    void removeArticleWithLogic(@Param("id") String id);

    String selectCategoryIdsByArticleId(@Param("id") String id);

    String selectNextOneId(@Param("id") String id, @Param("categoryId") String categoryId);

    String selectPreviousId(@Param("id") String id, @Param("categoryId") String categoryId);
}
