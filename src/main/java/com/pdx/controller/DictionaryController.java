package com.pdx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.pdx.service.DictionaryService;
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
@RequestMapping("/pdx/dic")
public class DictionaryController {

        @Autowired
        private DictionaryService dictionaryService;

}

