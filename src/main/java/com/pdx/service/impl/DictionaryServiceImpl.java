package com.pdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pdx.model.entity.Dictionary;
import com.pdx.mapper.DictionaryMapper;
import com.pdx.model.vo.DictionaryVo;
import com.pdx.response.Result;
import com.pdx.service.DictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdx.utils.CommonUtils;
import com.pdx.utils.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.pdx.constants.BasicConstants.OPERATE_RESULT;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Override
    public Result<?> searchPage(PageParams params) {
        Page<Dictionary> page = new Page<>(params.getCurrent(), params.getPageSize());
        QueryWrapper<Dictionary> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("update_time");
        Page<Dictionary> dictionaryPage = baseMapper.selectPage(page, wrapper);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dics", dictionaryPage.getRecords());
        resultMap.put("total", dictionaryPage.getTotal());
        return Result.success(resultMap);
    }

    @Override
    public Result<?> addDic(DictionaryVo vo) {
        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(vo, dictionary);
        dictionary.setId(CommonUtils.uuid());
        dictionary.setCreateTime(new Date());
        dictionary.setUpdateTime(new Date());
        int result = baseMapper.insert(dictionary);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> updateDic(DictionaryVo vo) {
        UpdateWrapper<Dictionary> updateWrapper = new UpdateWrapper<>();
        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(vo, dictionary);
        dictionary.setUpdateTime(new Date());
        updateWrapper.eq("id", vo.getId());
        int result = baseMapper.update(dictionary, updateWrapper);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> selectDicInfo(String id) {
        Dictionary dictionary = baseMapper.selectById(id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", dictionary);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> removeDicInfo(String id) {
        int result = baseMapper.deleteById(id);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }
}
