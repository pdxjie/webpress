package com.pdx.service;

import com.pdx.model.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdx.model.vo.UpdateBannerVo;
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
public interface BannerService extends IService<Banner> {

    /**
     * 轮播图列表
     *
     * @param params 分页参数
     * @return Result
     */
    Result<?> searchPages(PageParams params);

    /**
     * 添加 Banner
     *
     * @param banner Banner 信息
     * @return 操作结果
     */
    Result<?> addBanner(Banner banner);

    /**
     * 更新 Banner 信息
     * @param bannerVo 待更新信息
     * @return 操作结果
     */
    Result<?> updateBannerInfo(UpdateBannerVo bannerVo);

    /**
     * 获取 Banner 详情
     * @param id Banner ID
     * @return Banner 信息
     */
    Result<?> getBannerInfo(String id);

    /**
     * 删除 Banner 信息
     * @param id Banner ID
     * @return 操作结果
     */
    Result<?> removeBanner(String id);

    /**
     * 小程序显示 Banner
     * @param key 字典 key
     * @return Banner 数据
     */
    Result<?> showInApplet(String key);
}
