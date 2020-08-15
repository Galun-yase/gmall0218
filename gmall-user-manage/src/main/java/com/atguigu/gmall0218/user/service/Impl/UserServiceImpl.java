package com.atguigu.gmall0218.user.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0218.bean.UserAddress;
import com.atguigu.gmall0218.bean.UserInfo;
import com.atguigu.gmall0218.config.RedisUtil;
import com.atguigu.gmall0218.service.UserService;
import com.atguigu.gmall0218.user.mapper.UserAddressMapper;
import com.atguigu.gmall0218.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service//dubbo中的@service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserAddressMapper userAddressMapper;
    private final String userKey_prefix = "user";
    private final String userKey_suffix = ":info";
    public int userKey_timeOut = 60 * 60 * 24;
    @Autowired//自动装配容器中的Bean,在RedisConfig中@Bean加入的容器
            RedisUtil redisUtil;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        return userAddressMapper.select(userAddress);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(password);

        UserInfo info = userInfoMapper.selectOne(userInfo);
        if (info != null) {
            Jedis jedis = redisUtil.getJedis();
            jedis.setex(userKey_prefix + info.getId() + userKey_suffix, userKey_timeOut, JSON.toJSONString(info));
            jedis.close();
            return info;
        }
        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String userkey = userKey_prefix + userId + userKey_suffix;
        String userJson = jedis.get(userkey);
        //延长认证的时效
        jedis.expire(userkey, userKey_timeOut);
        if (!StringUtils.isEmpty(userJson)) {
            UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
            jedis.close();
            return userInfo;
        }
        jedis.close();
        return null;
    }
}
