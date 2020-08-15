package com.atguigu.gmall0218.service;

import com.atguigu.gmall0218.bean.UserAddress;
import com.atguigu.gmall0218.bean.UserInfo;

import java.util.List;

public interface UserService {

    List<UserInfo> findAll();

    List<UserAddress> getUserAddressList(String userId);

    UserInfo login(UserInfo userInfo);

    /**
     * description:根据userid查询userinfo
     */
    UserInfo verify(String userId);
}
