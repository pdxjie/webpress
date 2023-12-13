package com.pdx.service;

import com.pdx.model.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pdx.model.vo.ArticleSaveVo;
import com.pdx.model.vo.ArticleUpdateVo;
import com.pdx.model.vo.QueryArticleVo;
import com.pdx.model.vo.RestoreArticleVo;
import com.pdx.response.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
public interface ArticleService extends IService<Article> {

    Result<?> searchPage(QueryArticleVo vo);

    Result<?> saveInterviewQuestion(ArticleSaveVo vo);

    Result<?> updateInterviewQuestion(ArticleUpdateVo vo);

    Result<?> getInterviewQuestionInfo(String id);

    Result<?> removeInterviewQuestionById(String id);

    Result<?> preview(String id);

    Result<?> nextAndPre(String id, String type);

    Result<?> restoreArticles(RestoreArticleVo restoreArticleVo);
}
