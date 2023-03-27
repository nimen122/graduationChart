package com.example.graduationProject.service.impl;

import com.example.graduationProject.entity.Login;
import com.example.graduationProject.entity.SysUser;
import com.example.graduationProject.mapper.LoginMapper;
import com.example.graduationProject.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private LoginMapper loginMapper;
    @Override
    public SysUser findUserByAccount(String account) {
        return null;
    }
}
