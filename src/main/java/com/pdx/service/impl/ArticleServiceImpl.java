package com.pdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.pdx.mapper.CategoryMapper;
import com.pdx.mapper.LikeMapper;
import com.pdx.model.dto.ArticlePageDto;
import com.pdx.model.dto.RestoreArticleDto;
import com.pdx.model.entity.Article;
import com.pdx.mapper.ArticleMapper;
import com.pdx.model.entity.Category;
import com.pdx.model.entity.Like;
import com.pdx.model.vo.ArticleSaveVo;
import com.pdx.model.vo.ArticleUpdateVo;
import com.pdx.model.vo.QueryArticleVo;
import com.pdx.model.vo.RestoreArticleVo;
import com.pdx.response.Result;
import com.pdx.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdx.utils.CommonUtils;
import com.pdx.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.pdx.constants.BasicConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private SecurityUtil securityUtil;

    @Override
    public Result<?> searchPage(QueryArticleVo vo) {
        Page<Article> page = new Page<>(vo.getCurrent(), vo.getPageSize());
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        // 拼接参数
        if (StrUtil.isNotBlank(vo.getCategoryId())) {
            wrapper.eq("category_id", vo.getCategoryId());
        }
        if (StrUtil.isNotBlank(vo.getTitle())) {
            wrapper.eq("title", vo.getTitle());
        }
        if (null != vo.getType() && StrUtil.isNotBlank(String.valueOf(vo.getType()))) {
            wrapper.eq("type", vo.getType());
        }
        wrapper.orderByDesc("update_time").eq("is_deleted", false);
        Page<Article> articlePage = baseMapper.selectPage(page, wrapper);
        List<Article> records = articlePage.getRecords();
        List<ArticlePageDto> dtos = new ArrayList<>();
        records.forEach(article -> {
            ArticlePageDto articlePageDto = new ArticlePageDto();
            BeanUtils.copyProperties(article, articlePageDto);
            articlePageDto.setArticleTitle(article.getTitle());
            // 查询分类
            String name = categoryMapper.selectOne(new QueryWrapper<Category>()
                                        .eq("id", article.getCategoryId())
                                        .select("name")).getName();
            articlePageDto.setCateName(name);
            // 查询当前用户是否点赞是否点赞
            Integer count = likeMapper.selectCount(new QueryWrapper<Like>().eq("user_id", securityUtil.getCurrentUserId()).eq("article_id", article.getId()));
            articlePageDto.setLike(Objects.equals(count, OPERATE_RESULT));
            dtos.add(articlePageDto);
        });
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("articles", dtos);
        resultMap.put("total", articlePage.getTotal());
        return Result.success(resultMap);
    }

    @Override
    public Result<?> saveInterviewQuestion(ArticleSaveVo vo) {
        Article article = new Article();
        BeanUtils.copyProperties(vo, article);
        article.setId(CommonUtils.uuid());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        int result = baseMapper.insert(article);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> updateInterviewQuestion(ArticleUpdateVo vo) {
        Article article = new Article();
        BeanUtils.copyProperties(vo, article);
        article.setUpdateTime(new Date());
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", vo.getId());
        int result = baseMapper.update(article, updateWrapper);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> getInterviewQuestionInfo(String id) {
        Article article = baseMapper.selectById(id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("article", article);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> removeInterviewQuestionById(String id) {
        baseMapper.removeArticleWithLogic(id);
        return Result.success();
    }

    @Override
    public Result<?> preview(String id) {
        baseMapper.increViewCount(id);
        return Result.success();
    }

    @Override
    public Result<?> nextAndPre(String id, String type) {
        String categoryId = baseMapper.selectCategoryIdsByArticleId(id);
        String articleId = null;
        if (NEXT_ONE.equalsIgnoreCase(type)) {
            articleId = baseMapper.selectNextOneId(id, categoryId);

        } else if (PREVIOUS_ONE.equalsIgnoreCase(type)) {
            articleId = baseMapper.selectPreviousId(id, categoryId);
        }
        return Result.success(articleId);
    }

    @Override
    public Result<?> restoreArticles(RestoreArticleVo restoreArticleVo) {
        List<String> successIds = new ArrayList<>();
        List<String> errorIds = new ArrayList<>();
        for (String articleId : restoreArticleVo.getArticleIds()) {
            try {
                UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
                Article article = new Article();
                article.setDeleted(false);
                updateWrapper.eq("id", articleId);
                int result = baseMapper.update(article, updateWrapper);
                if (Objects.equals(result, OPERATE_RESULT)) {
                    successIds.add(articleId);
                }
            } catch (Exception e) {
                errorIds.add(articleId);
            }
        }
        RestoreArticleDto restoreArticleDto = new RestoreArticleDto();
        restoreArticleDto.setErrorIds(errorIds);
        restoreArticleDto.setSuccessIds(successIds);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", restoreArticleDto);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> recommend(String id) {
        Article article = baseMapper.selectById(id);
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("is_recommend", !article.isRecommend());
        int result = baseMapper.update(null, updateWrapper);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }
}
