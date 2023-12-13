package com.pdx.controller;

import com.pdx.model.entity.Comment;
import com.pdx.response.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import com.pdx.service.CommentService;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@Api(tags = "评论模块相关接口")
@RestController
@RequestMapping("/pdx/comment")
public class CommentController {

        @Autowired
        private CommentService commentService;

}

