package com.kidand.dianping.controller;

import com.kidand.dianping.common.BusinessException;
import com.kidand.dianping.common.CommonError;
import com.kidand.dianping.common.CommonRes;
import com.kidand.dianping.common.EmBusinessError;
import com.kidand.dianping.model.UserModel;
import com.kidand.dianping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/user")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return "test";
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonRes getUser(@RequestParam(name="id")Integer id) throws BusinessException {
        UserModel userModel =  userService.getUser(id);
        if(userModel==null){
            //return CommonRes.create(new CommonError(EmBusinessError.NO_OBJECT_FOUND),"fail");
            throw new BusinessException(EmBusinessError.NO_OBJECT_FOUND);
        }else{
            return CommonRes.create(userModel);
        }
    }
}
