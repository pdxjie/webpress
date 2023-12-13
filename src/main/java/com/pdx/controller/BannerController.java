package com.pdx.controller;

import com.pdx.model.entity.Banner;
import com.pdx.model.vo.UpdateBannerVo;
import com.pdx.response.Result;
import com.pdx.utils.PageParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import com.pdx.service.BannerService;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Api(tags = "轮播图模块相关接口")
@RestController
@RequestMapping("/pdx/banner")
public class BannerController {

        @Autowired
        private BannerService bannerService;

        @PostMapping("/pages")
        @ApiOperation(value = "轮播图列表 —— 管理端")
        public Result<?> pages(@RequestBody PageParams params) {
            return bannerService.searchPages(params);
        }


        @PostMapping("/add")
        @ApiOperation(value = "添加 Banner —— 管理端")
        public Result<?> addBanner(@RequestBody Banner banner) {
            return bannerService.addBanner(banner);
        }

        @PostMapping("/edit")
        @ApiOperation(value = "更新 Banner —— 管理端")
        public Result<?> updateBanner(@RequestBody @Valid UpdateBannerVo bannerVo) {
            return bannerService.updateBannerInfo(bannerVo);
        }

        @GetMapping("/{id}")
        @ApiOperation(value = "获取 Banner 详情 —— 管理端")
        public Result<?> getBannerInfo(@PathVariable("id") String id) {
            return bannerService.getBannerInfo(id);
        }

        @DeleteMapping("/{id}")
        @ApiOperation(value = "删除 Banner 信息 —— 管理端")
        public Result<?> removeBanner(@PathVariable("id") String id) {
            return bannerService.removeBanner(id);
        }

        @GetMapping("/show/{key}")
        @ApiOperation(value = "小程序 Banner")
        public Result<?> showInApplet(@PathVariable("key") String key) {
           return bannerService.showInApplet(key);
        }
}

