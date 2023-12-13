package com.pdx.service;

import com.pdx.model.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdx.model.vo.CategoryVo;
import com.pdx.model.vo.QueryCatePageVo;
import com.pdx.response.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
public interface CategoryService extends IService<Category> {

    Result<?> searchPage(QueryCatePageVo vo);

    Result<?> addCate(CategoryVo vo);

    Result<?> updateCate(CategoryVo vo);

    Result<?> selectCateInfo(String id);

    Result<?> removeCateInfo(String id);

    Result<?> getChildCateInfo();

    Result<?> treeCate();

}
