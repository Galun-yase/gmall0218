package com.atguigu.gmall0218.order.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0218.bean.CartInfo;
import com.atguigu.gmall0218.bean.OrderDetail;
import com.atguigu.gmall0218.bean.OrderInfo;
import com.atguigu.gmall0218.bean.SkuInfo;
import com.atguigu.gmall0218.bean.UserAddress;
import com.atguigu.gmall0218.config.LoginRequire;
import com.atguigu.gmall0218.service.CartService;
import com.atguigu.gmall0218.service.ManageService;
import com.atguigu.gmall0218.service.OrderService;
import com.atguigu.gmall0218.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    private UserService userService;
    @Reference
    private CartService cartService;
    @Reference
    private OrderService orderService;
    @Reference
    private ManageService manageService;


    @RequestMapping("trade")
    //@LoginRequire
    public String trade(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");

        //用户地址
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        request.setAttribute("userAddressList", userAddressList);

        // 展示送货清单：
        // 数据来源：勾选的购物车！user:userId:checked！
        List<CartInfo> cartInfoList = cartService.getCartCheckedList(userId);

        // 声明一个集合来存储订单明细
        ArrayList<OrderDetail> orderDetailArrayList = new ArrayList<>();
        // 将集合数据赋值OrderDetail
        for (CartInfo cartInfo : cartInfoList) {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());

            orderDetailArrayList.add(orderDetail);
        }

        // 总金额：
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailArrayList);
        // 调用计算总金额的方法  {totalAmount}
        orderInfo.sumTotalAmount();

        request.setAttribute("totalAmount", orderInfo.getTotalAmount());
        // 保存送货清单集合
        request.setAttribute("orderDetailArrayList", orderDetailArrayList);

        //生成一个流水号，放入页面 同时放入缓存  刷新不会重新放入缓存
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo", tradeNo);
        return "trade";
    }

    @RequestMapping("submitOrder")
    @LoginRequire
    public String submitOrder(HttpServletRequest request, OrderInfo orderInfo) {
        String userId = (String) request.getAttribute("userId");
        // orderInfo 中还缺少一个userId
        orderInfo.setUserId(userId);

        // 判断是否是重复提交
        // 先获取页面的流水号
        String tradeNo = request.getParameter("tradeNo");
        // 调用比较方法
        boolean result = orderService.checkTradeCode(userId, tradeNo);
        // 是重复提交
        if (!result) {
            request.setAttribute("errMsg", "订单已提交，不能重复提交！");
            return "tradeFail";
        }

        // 验证库存！ 验证实时价格
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            boolean flag = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!flag) {
                request.setAttribute("errMsg", orderDetail.getSkuName() + "商品库存不足！");
                return "tradeFail";
            }
            // 获取skuInfo 对象b
            SkuInfo skuInfo = manageService.getSkuInfo(orderDetail.getSkuId());
            //
            int res = skuInfo.getPrice().compareTo(orderDetail.getOrderPrice());
            if (res != 0) {
                request.setAttribute("errMsg", orderDetail.getSkuName() + "价格不匹配");
                cartService.loadCartCache(userId);
                return "tradeFail";
            }
        }

        // 调用服务层
        String orderId = orderService.saveOrder(orderInfo);

        // 删除流水号
        orderService.delTradeCode(userId);
        // 支付
        return "redirect://localhost:8091/index?orderId=" + orderId;
    }

}
