package com.pdx.service;

import com.pdx.model.entity.Dictionary;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdx.model.vo.DictionaryVo;
import com.pdx.response.Result;
import com.pdx.utils.PageParams;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
public interface DictionaryService extends IService<Dictionary> {

    Result<?> searchPage(PageParams params);

    Result<?> addDic(DictionaryVo vo);

    Result<?> updateDic(DictionaryVo vo);

    Result<?> selectDicInfo(String id);

    Result<?> removeDicInfo(String id);
}
