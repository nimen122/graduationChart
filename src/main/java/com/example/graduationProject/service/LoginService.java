package com.example.graduationProject.service;

import com.example.graduationProject.entity.Login;
import com.example.graduationProject.mapper.LoginMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class LoginService {
    @Resource
    private LoginMapper loginMapper;

    public List<Login> selectByAccount(String loginAccount){
        return loginMapper.selectByAccount(loginAccount);
    }

    public boolean insertAccount(Login login){
        loginMapper.insertAccount(login);
        return true;
    }

    public boolean updateAccount(Login newlogin) {
        return loginMapper.updateAccount(newlogin);
    }

    public boolean deleteAccount(String loginAccount) {
        return loginMapper.deleteAccount(loginAccount);
    }
}
