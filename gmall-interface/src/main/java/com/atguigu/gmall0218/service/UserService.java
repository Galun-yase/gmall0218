package com.atguigu.gmall0218.service;

import com.atguigu.gmall0218.bean.UserAddress;
import com.atguigu.gmall0218.bean.UserInfo;

import java.util.List;

public interface UserService {

    List<UserInfo> findAll();

    public List<UserAddress> getUserAddressList(String userId);

}
