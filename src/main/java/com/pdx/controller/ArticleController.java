package com.pdx.controller;

import com.pdx.annotation.CheckRoles;
import com.pdx.constants.RoleType;
import com.pdx.model.vo.ArticleSaveVo;
import com.pdx.model.vo.ArticleUpdateVo;
import com.pdx.model.vo.QueryArticleVo;
import com.pdx.model.vo.RestoreArticleVo;
import com.pdx.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.pdx.service.ArticleService;

import javax.websocket.server.PathParam;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Api(tags = "文章模块相关接口")
@RestController
@RequestMapping("/pdx/article")
public class ArticleController {

        @Autowired
        private ArticleService articleService;


        @ApiOperation(value = "条件查询八股题库接口")
        @CheckRoles(value = RoleType.ADMIN)
        @PostMapping("/pages")
        public Result<?> pages (@RequestBody QueryArticleVo vo) {
                return articleService.searchPage(vo);
        }

        @PostMapping("/insert")
        @ApiOperation(value = "爆料八股")
        public Result<?> insertInterviewQuestion (@RequestBody ArticleSaveVo vo) {
                return articleService.saveInterviewQuestion(vo);
        }

        @PostMapping("/update")
        @ApiOperation(value = "修改八股信息")
        public Result<?> updateInterviewQuestion (@RequestBody ArticleUpdateVo vo) {
                return articleService.updateInterviewQuestion(vo);
        }

        @GetMapping("/{id}")
        @ApiOperation(value = "获取八股详情")
        public Result<?> interviewQuestionInfo (@PathVariable("id") String id) {
                return articleService.getInterviewQuestionInfo(id);
        }

        @DeleteMapping("/{id}")
        @ApiOperation(value = "删除八股题库")
        public Result<?> removeById (@PathVariable("id") String id) {
                return articleService.removeInterviewQuestionById(id);
        }

        @PostMapping("/restore")
        @ApiOperation(value = "回收站恢复")
        public Result<?> restoreArticle(@RequestBody RestoreArticleVo restoreArticleVo) {
                return articleService.restoreArticles(restoreArticleVo);
        }

        @GetMapping("/preview/{id}")
        @ApiOperation(value = "用户观看")
        public Result<?> preview (@PathVariable("id") String id) {
                return articleService.preview(id);
        }


        @GetMapping("/next")
        @ApiOperation(value = "上一篇 下一篇 八股文")
        public Result<?> nextAndPre (@PathParam("id") String id, @PathParam("type") String type) {
                return articleService.nextAndPre(id, type);
        }
}

