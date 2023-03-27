package com.example.graduationProject.service;

import com.example.graduationProject.entity.SysUser;

public interface SysUserService {

    SysUser findUserByAccount(String account);

}
