package com.example.graduationProject.controller;

import com.example.graduationProject.Md5;
import com.example.graduationProject.entity.Login;
import com.example.graduationProject.service.LoginService;
import com.example.graduationProject.vo.Result;
import com.example.graduationProject.vo.params.LoginParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/Login")
@Api(value = "登录接口", tags = "登录接口" )
public class LoginController2 {

    @Autowired
    private LoginService loginService;

    /**
     * 登录验证
     * @param loginParam
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "登录验证",notes = "登录验证")
    public Result login(@RequestBody LoginParam loginParam) {
        log.info("开始验证...");
        return null;

    }


}
