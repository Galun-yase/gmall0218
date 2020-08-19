package com.atguigu.gmall0218.order.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0218.bean.OrderDetail;
import com.atguigu.gmall0218.bean.OrderInfo;
import com.atguigu.gmall0218.bean.enums.OrderStatus;
import com.atguigu.gmall0218.bean.enums.ProcessStatus;
import com.atguigu.gmall0218.config.ActiveMQUtil;
import com.atguigu.gmall0218.config.RedisUtil;
import com.atguigu.gmall0218.order.mapper.OrderDetailMapper;
import com.atguigu.gmall0218.order.mapper.OrderInfoMapper;
import com.atguigu.gmall0218.service.OrderService;
import com.atguigu.gmall0218.util.HttpClientUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private ActiveMQUtil activeMQUtil;

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

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);

        List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);

        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    @Override
    public void updateOrderStatus(String orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setProcessStatus(processStatus);
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(processStatus.getOrderStatus());
        orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
    }

    @Override
    public void sendOrderStatus(String orderId) {
        Connection connection = activeMQUtil.getConnection();
        //初始化仓储需要接受的消息
        String orderInfoJson = initWareOrder(orderId);
        try {
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            // 创建队列
            Queue order_result_queue = session.createQueue("ORDER_RESULT_QUEUE");
            // 创建消息提供者
            MessageProducer producer = session.createProducer(order_result_queue);

            // 创建消息对象
            ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
            // orderInfo 组成json 字符串
            activeMQTextMessage.setText(orderInfoJson);

            producer.send(activeMQTextMessage);
            // 提交
            session.commit();

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据orderId 将orderInfo 变为json 字符串
     *
     * @param orderId
     * @return
     */
    private String initWareOrder(String orderId) {
        // 根据orderId 查询orderInfo
        OrderInfo orderInfo = getOrderInfo(orderId);
        // 将orderInfo 中有用的信息保存到map 中！
        Map map = initWareOrder(orderInfo);
        // 将map 转换为json  字符串！
        return JSON.toJSONString(map);
    }

    /**
     * @param orderInfo
     * @return
     */
    private Map initWareOrder(OrderInfo orderInfo) {
        HashMap<String, Object> map = new HashMap<>();
        // 给map 的key 赋值！
        map.put("orderId", orderInfo.getId());
        map.put("consignee", orderInfo.getConsignee());
        map.put("consigneeTel", orderInfo.getConsigneeTel());
        map.put("orderComment", orderInfo.getOrderComment());
        map.put("orderBody", "测试用例");
        map.put("deliveryAddress", orderInfo.getDeliveryAddress());
        map.put("paymentWay", "2");

        // map.put("wareId",orderInfo.getWareId()); 仓库Id
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        // 创建一个集合来存储map
        ArrayList<Map> arrayList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            HashMap<String, Object> orderDetailMap = new HashMap<>();
            orderDetailMap.put("skuId", orderDetail.getSkuId());
            orderDetailMap.put("skuNum", orderDetail.getSkuNum());
            orderDetailMap.put("skuName", orderDetail.getSkuName());
            arrayList.add(orderDetailMap);
        }
        map.put("details", arrayList);

        return map;
    }
}
