package com.kidand.dianping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/user")
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "test";
    }
}
