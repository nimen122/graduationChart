package com.example.graduationProject.mapper;

import com.example.graduationProject.entity.Login;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginMapper {
    List<Login> selectByAccount(@Param("loginAccount") String loginAccount);

    void insertAccount(Login login);

    boolean updateAccount(Login newlogin);

    boolean deleteAccount(@Param("loginAccount") String loginAccount);

//    Login findUserById(int id);
}
