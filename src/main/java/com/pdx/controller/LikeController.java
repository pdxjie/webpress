package com.pdx.controller;

import com.pdx.model.entity.Like;
import com.pdx.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import com.pdx.service.LikeService;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author IT 派同学
 * @since 2023-12-12
 */
@RestController
@RequestMapping("/pdx/like")
public class LikeController {

        @Autowired
        private LikeService likeService;
}

