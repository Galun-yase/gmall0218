package com.atguigu.gmall0218.order.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0218.bean.OrderDetail;
import com.atguigu.gmall0218.bean.OrderInfo;
import com.atguigu.gmall0218.bean.enums.OrderStatus;
import com.atguigu.gmall0218.bean.enums.ProcessStatus;
import com.atguigu.gmall0218.config.RedisUtil;
import com.atguigu.gmall0218.order.mapper.OrderDetailMapper;
import com.atguigu.gmall0218.order.mapper.OrderInfoMapper;
import com.atguigu.gmall0218.service.OrderService;
import com.atguigu.gmall0218.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author 任青成
 * @date 2020/8/17 16:51
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional
    public String saveOrder(OrderInfo orderInfo) {
        // 数据不完整！总金额，订单状态，第三方交易编号，创建时间，过期时间，进程状态
        // 总金额
        orderInfo.sumTotalAmount();
        // 创建时间
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        // 第三方交易编号
        String outTradeNo = "ATGUIGU" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        // 创建时间
        orderInfo.setCreateTime(new Date());
        // 过期时间 +1
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());

        // 进程状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        // 只保存了一份订单
        orderInfoMapper.insertSelective(orderInfo);

        // 订单明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            // 设置orderId
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);
        }

        return orderInfo.getId();
    }

    @Override//生成流水号
    public String getTradeNo(String userId) {
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String tradeNoKey = "user:" + userId + ":tradeCode";
        // 定义一个流水号
        String tradeNo = UUID.randomUUID().toString();
        // String类型
        jedis.set(tradeNoKey, tradeNo);

        jedis.close();

        return tradeNo;
    }

    @Override
    public boolean checkTradeCode(String userId, String tradeCodeNo) {
        // 获取缓存的流水号
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String tradeNoKey = "user:" + userId + ":tradeCode";
        // 获取数据
        String tradeNo = jedis.get(tradeNoKey);
        // 关闭
        jedis.close();
        return tradeCodeNo.equals(tradeNo);
    }

    @Override
    public void delTradeCode(String userId) {
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String tradeNoKey = "user:" + userId + ":tradeCode";
        // 删除
        jedis.del(tradeNoKey);
        jedis.close();

    }

    @Override
    public boolean checkStock(String skuId, Integer skuNum) {
        String result = HttpClientUtil.doGet("http://localhost:8090/hasStock?skuId=" + skuId + "&num=" + skuNum);
        return "1".equals(result);
    }

    @Override
    public OrderInfo getOrderInfo(String orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);
        return orderInfo;
    }
}
