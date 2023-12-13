package com.pdx.controller;

import com.pdx.model.vo.DictionaryVo;
import com.pdx.response.Result;
import com.pdx.utils.PageParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pdx.service.DictionaryService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Api(tags = "字典模块相关接口")
@RestController
@RequestMapping("/pdx/dic")
public class DictionaryController {

        @Autowired
        private DictionaryService dictionaryService;

        @ApiOperation(value = "分页查询字典列表")
        @PostMapping("/page")
        public Result<?> searchPage(@RequestBody PageParams params) {
           return dictionaryService.searchPage(params);
        }

        @ApiOperation(value = "添加字典")
        @PostMapping("/add")
        public Result<?> addDic(@RequestBody DictionaryVo vo) {
           return dictionaryService.addDic(vo);
        }

        @ApiOperation(value = "更新字典")
        @PostMapping("/update")
        public Result<?> updateDic(@RequestBody DictionaryVo vo) {
           return dictionaryService.updateDic(vo);
        }

        @ApiOperation(value = "获取字典信息")
        @GetMapping("/{id}")
        public Result<?> dicInfo(@PathVariable("id") String id) {
           return dictionaryService.selectDicInfo(id);
        }

        @ApiOperation(value = "删除字典")
        @DeleteMapping("/{id}")
        public Result<?> removeDicInfo(@PathVariable("id") String id) {
          return dictionaryService.removeDicInfo(id);
        }

}

