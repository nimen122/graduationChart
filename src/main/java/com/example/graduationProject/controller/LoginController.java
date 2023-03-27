package com.example.graduationProject.controller;

import com.example.graduationProject.Md5;
import com.example.graduationProject.entity.Login;
import com.example.graduationProject.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/Login")
@Api(value = "登录接口", tags = "登录接口" )
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/selectByAccount", method = RequestMethod.POST)
    @ApiOperation(value = "查询登录情况",notes = "查询登录情况")
    public List<Login> selectByAccount(@RequestBody Login login) {
        log.info("开始查询...");
        List<Login> logins = loginService.selectByAccount(login.getLoginAccount());
        if(logins.size()==0){
            log.info("查无此人");
            return logins;
        }else if(!logins.get(0).getLoginPassword().equals(Md5.code(login.getLoginPassword()))){
            log.info("密码错误");
            return new ArrayList<>();
        }else {
            log.info("成功登录");
            logins.get(0).setLoginPassword("");
            return logins;
        }
    }

    @RequestMapping(value = "/insertAccount", method = RequestMethod.POST)
    @ApiOperation(value = "新增用户信息", notes = "新增用户信息")
    @CrossOrigin //跨域请求
    public boolean insertAccount(@RequestBody Login login) {
        log.info("开始新增...={}",login);
        List<Login> logins = loginService.selectByAccount(login.getLoginAccount());
        if(logins.size()==0){
            String getPassword = Md5.code(login.getLoginPassword());
            login.setLoginPassword(getPassword);
            return loginService.insertAccount(login);
        }else return false;

    }
    @RequestMapping(value = "/updateAccount", method = RequestMethod.PUT)
    @ApiOperation(value = "更新账号", notes = "更新账号")
    @CrossOrigin //跨域请求
    public boolean updateAccount(@RequestBody Login newlogin) {
        log.info("开始更新...{}",newlogin);
        List<Login> logins = loginService.selectByAccount(newlogin.getLoginAccount());
        if(logins.size()==1){
            String getPassword = Md5.code(newlogin.getLoginPassword());
            newlogin.setLoginPassword(getPassword);
            return loginService.updateAccount(newlogin);
        }else return false;

    }
    @RequestMapping(value = "/deleteLogin", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除账号", notes = "删除账号")
    @CrossOrigin //跨域请求
    public boolean deleteAccount( @RequestParam(value = "loginAccount") String loginAccount) {
        log.info("开始删除...{}",loginAccount);
        List<Login> logins = loginService.selectByAccount(loginAccount);
        if (logins.size()==1){
            return loginService.deleteAccount(loginAccount);
        }else return false;
    }

}
