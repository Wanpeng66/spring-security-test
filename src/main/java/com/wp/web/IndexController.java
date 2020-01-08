package com.wp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: wp
 * @Title: IndexController
 * @Description: TODO
 * @date 2020/1/3 11:11
 */
@Controller
public class IndexController {

    //@GetMapping("/")
    public String index(){
        return "index";
    }

    //@GetMapping("/index")
    public String toIndex(){
        return "index";
    }


}
