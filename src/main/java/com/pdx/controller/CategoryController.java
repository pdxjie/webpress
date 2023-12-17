package com.pdx.controller;

import com.pdx.model.vo.CategoryVo;
import com.pdx.model.vo.QueryCatePageVo;
import com.pdx.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import com.pdx.service.CategoryService;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Api(tags = "分类模块相关接口")
@RestController
@RequestMapping("/pdx/category")
public class CategoryController {

        @Autowired
        private CategoryService categoryService;

        @ApiOperation(value = "分类分页查询")
        @PostMapping("/page")
        public Result<?> searchPage(@RequestBody QueryCatePageVo vo) {
           return categoryService.searchPage(vo);
        }

        @ApiOperation(value = "添加分类")
        @PostMapping("/add")
        public Result<?> addDic(@RequestBody CategoryVo vo) {
           return categoryService.addCate(vo);
        }

        @ApiOperation(value = "更新分类")
        @PostMapping("/update")
        public Result<?> updateDic(@RequestBody CategoryVo vo) {
           return categoryService.updateCate(vo);
        }

        @ApiOperation(value = "获取分类信息")
        @GetMapping("/{id}")
        public Result<?> dicInfo(@PathVariable("id") String id) {
           return categoryService.selectCateInfo(id);
        }

        @ApiOperation(value = "删除分类")
        @DeleteMapping("/{id}")
        public Result<?> removeDicInfo(@PathVariable("id") String id) {
           return categoryService.removeCateInfo(id);
        }

        @ApiOperation(value = "获取分类数据")
        @GetMapping("/tree")
        public Result<?> treeCate() {
           return categoryService.treeCate();
        }

        @ApiOperation(value = "获取所有的二级分类")
        @GetMapping("/twice")
        public Result<?> getChildCateInfo() {
           return categoryService.getChildCateInfo();
        }

        @ApiOperation(value = "获取所有的父级")
        @GetMapping("/parent")
        public Result<?> allParents() {
           return categoryService.allParents();
        }

}

