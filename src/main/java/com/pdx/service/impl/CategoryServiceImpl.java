package com.pdx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.pdx.mapper.ArticleMapper;
import com.pdx.model.dto.CategoryDto;
import com.pdx.model.dto.TreeCateDto;
import com.pdx.model.entity.Article;
import com.pdx.model.entity.Category;
import com.pdx.mapper.CategoryMapper;
import com.pdx.model.vo.CategoryVo;
import com.pdx.model.vo.QueryCatePageVo;
import com.pdx.response.Result;
import com.pdx.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pdx.utils.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.pdx.constants.BasicConstants.OPERATE_RESULT;
import static com.pdx.constants.BasicConstants.PARENT_CATE;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Result<?> searchPage(QueryCatePageVo vo) {
        Page<Category> page = new Page<>(vo.getCurrent(), vo.getPageSize());
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(vo.getCateName())) {
            wrapper.like("name", vo.getCateName());
        }
        wrapper.orderByDesc("update_time");
        Page<Category> categoryPage = baseMapper.selectPage(page, wrapper);
        List<CategoryDto> categoryDtos = new ArrayList<>();
        categoryPage.getRecords().forEach(category -> {
            CategoryDto categoryDto = new CategoryDto();
            BeanUtils.copyProperties(category, categoryDto);
            if (!category.getPid().equalsIgnoreCase(PARENT_CATE)) {
                String parentName = baseMapper.selectParentName(category.getPid());
                categoryDto.setParentName(parentName);
            } else {
                categoryDto.setParentName("无");
            }
            categoryDtos.add(categoryDto);
        });
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("cates", categoryDtos);
        resultMap.put("total", categoryPage.getTotal());
        return Result.success(resultMap);
    }

    @Override
    public Result<?> addCate(CategoryVo vo) {
        Category category = new Category();
        BeanUtils.copyProperties(vo, category);
        category.setId(CommonUtils.uuid());
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        int result = baseMapper.insert(category);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> updateCate(CategoryVo vo) {
        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<>();
        Category category = new Category();
        BeanUtils.copyProperties(vo, category);
        category.setUpdateTime(new Date());
        updateWrapper.eq("id", vo.getId());
        int result = baseMapper.update(category, updateWrapper);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> selectCateInfo(String id) {
        Category category = baseMapper.selectById(id);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", category);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> removeCateInfo(String id) {
        int result = baseMapper.deleteById(id);
        return Objects.equals(result, OPERATE_RESULT) ? Result.success() : Result.fail();
    }

    @Override
    public Result<?> getChildCateInfo() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.ne("pid", PARENT_CATE);
        List<Category> categories = baseMapper.selectList(wrapper);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", categories);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> treeCate() {
        // 获取所有的数据
        List<Category> categories = baseMapper.selectList(null);
        List<TreeCateDto> cateDtos = new ArrayList<>();
        categories.forEach(category -> {
            TreeCateDto treeCateDto = new TreeCateDto();
            BeanUtils.copyProperties(category, treeCateDto);
            cateDtos.add(treeCateDto);
        });
        // 递归封装分类数据
        List<TreeCateDto> collect = cateDtos.stream().filter(cate -> {
            return Objects.equals(cate.getPid(), PARENT_CATE);
        }).map((category) -> {
            category.setChildren(getChildrenCate(category, cateDtos));
            return category;
        }).collect(Collectors.toList());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", collect);
        return Result.success(resultMap);
    }

    @Override
    public Result<?> allParents() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", PARENT_CATE);
        List<Category> categories = baseMapper.selectList(wrapper);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", categories);
        return Result.success(resultMap);
    }

    /**
     * 获取子集
     *
     * @param category 父级分类
     * @param cateDtos 所有数据
     * @return 分类数据
     */
    private List<TreeCateDto> getChildrenCate(TreeCateDto category, List<TreeCateDto> cateDtos) {
        List<TreeCateDto> children = cateDtos.stream().filter(treeCateDto -> {
            return treeCateDto.getPid().equalsIgnoreCase(category.getId());
        }).map((cate) -> {
            // 查询出当前分类下的文章数
            Integer count = articleMapper.selectCount(new QueryWrapper<Article>().eq("category_id", cate.getId()));
            cate.setCount(count);
            cate.setChildren(getChildrenCate(cate, cateDtos));
            return cate;
        }).collect(Collectors.toList());
        return children;
    }
}
