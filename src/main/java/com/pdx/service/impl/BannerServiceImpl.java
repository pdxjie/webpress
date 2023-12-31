package com.pdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.pdx.mapper.ArticleMapper;
import com.pdx.model.dto.BannerDto;
import com.pdx.model.entity.Article;
import com.pdx.model.entity.Dictionary;
import com.pdx.mapper.DictionaryMapper;
import com.pdx.model.entity.Banner;
import com.pdx.mapper.BannerMapper;
import com.pdx.model.vo.UpdateBannerVo;
import com.pdx.response.Result;
import com.pdx.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdx.utils.CommonUtils;
import com.pdx.utils.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Resource
    private DictionaryMapper dictionaryMapper;

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 轮播图列表
     * @param params 分页参数
     * @return Result
     */
    @Override
    public Result<?> searchPages(PageParams params) {
        Page<Banner> page = new Page<>(params.getCurrent(), params.getPageSize());
        Page<Banner> bannerPage = this.baseMapper.selectPage(page, null);
        List<BannerDto> bannerDtos = new ArrayList<>();
        bannerPage.getRecords().forEach(banner -> {
            BannerDto bannerDto = new BannerDto();
            BeanUtils.copyProperties(banner, bannerDto);
            if (StrUtil.isNotBlank(banner.getArticleId())) {
                Article article = articleMapper.selectArticleNameAndTypeById(banner.getArticleId());
                bannerDto.setArticleName(article.getTitle());
                bannerDto.setType(article.getType());
            } else {
                bannerDto.setArticleName("未关联");
                bannerDto.setType(2);
            }
            bannerDtos.add(bannerDto);
        });
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", bannerPage.getTotal());
            resultMap.put("banners", bannerDtos);
        return Result.success(resultMap);
    }

    /**
     * 添加 Banner
     * @param vo Banner 信息
     * @return 操作结果
     */
    @Override
    public Result<?> addBanner(UpdateBannerVo vo) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(vo, banner);
        banner.setId(CommonUtils.uuid());
        banner.setCreateTime(new Date());
        banner.setUpdateTime(new Date());
        int result = this.baseMapper.insert(banner);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    /**
     * 更新 Banner 信息
     * @param bannerVo 待更新信息
     * @return 操作结果
     */
    @Override
    public Result<?> updateBannerInfo(UpdateBannerVo bannerVo) {
        UpdateWrapper<Banner> wrapper = new UpdateWrapper<>();
        Banner banner = new Banner();
        BeanUtils.copyProperties(bannerVo, banner);
        wrapper.eq("id", bannerVo.getBannerId());
        int update = this.baseMapper.update(banner, wrapper);
        return Objects.equals(update, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    /**
     * 获取 Banner 详情
     * @param id Banner ID
     * @return Banner 信息
     */
    @Override
    public Result<?> getBannerInfo(String id) {
        BannerDto bannerDto = new BannerDto();
        Banner banner = this.baseMapper.selectById(id);
        BeanUtils.copyProperties(banner, bannerDto);
        if (StrUtil.isNotBlank(banner.getArticleId())) {
            Article article = articleMapper.selectArticleNameAndTypeById(banner.getArticleId());
            bannerDto.setArticleName(article.getTitle());
            bannerDto.setType(article.getType());
        } else {
            bannerDto.setArticleName("未关联");
            bannerDto.setType(2);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("bannerInfo", bannerDto);
        return Result.success(resultMap);
    }

    /**
     * 删除 Banner 信息
     * @param id Banner ID
     * @return 操作结果
     */
    @Override
    public Result<?> removeBanner(String id) {
        int result = this.baseMapper.deleteById(id);
        return Result.success(Objects.equals(result, OPERATE_RESULT));
    }

    /**
     * 小程序显示 Banner
     * @param key 字典 key
     * @return Banner 数据
     */
    @Override
    public Result<?> showInApplet(String key) {
        QueryWrapper<Dictionary> wrapper = new QueryWrapper<>();
        wrapper.eq("dic_key", key).select("dic_value");
        String dicValue = dictionaryMapper.selectOne(wrapper).getDicValue();
        Page<Banner> page = new Page<>(OPERATE_RESULT, Integer.parseInt(dicValue));
        Page<Banner> bannerPage = this.baseMapper.selectPage(page, null);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("banners", bannerPage.getRecords());
        return Result.success(resultMap);
    }
}
