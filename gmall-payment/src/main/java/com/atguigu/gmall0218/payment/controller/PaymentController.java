package com.atguigu.gmall0218.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall0218.bean.OrderInfo;
import com.atguigu.gmall0218.bean.PaymentInfo;
import com.atguigu.gmall0218.bean.enums.PaymentStatus;
import com.atguigu.gmall0218.payment.config.AlipayConfig;
import com.atguigu.gmall0218.service.OrderService;
import com.atguigu.gmall0218.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 任青成
 * @date 2020/8/17 23:57
 */
@Controller
public class PaymentController {

    @Autowired
    AlipayClient alipayClient;
    @Reference
    private OrderService orderService;
    @Reference
    private PaymentService paymentService;

    /**
     * 选择付款方式页面
     */
    @RequestMapping("index")
    public String index(String orderId, HttpServletRequest request) {
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);

        request.setAttribute("orderId", orderId);
        request.setAttribute("totalAmount", orderInfo.getTotalAmount());

        return "index";
    }

    /**
     * 传参数给支付页面，生成二维码
     */
    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipaySubmit(HttpServletRequest request, HttpServletResponse response) {
        /*
            1.  保存支付记录下 将数据放入数据库
                去重复，对账！ 幂等性=保证每一笔交易只能交易一次 {第三方交易编号outTradeNo}！
                paymentInfo
            2.  生成二维码
         */

        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);

        // paymentInfo 数据来源于谁！orderInfo
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(orderId);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject("给韩鹏买xxx！");
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        paymentInfo.setCreateTime(new Date());
        paymentService.savePaymentInfo(paymentInfo);

        // 生成二维码！
        // alipay.trade.page.pay
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        // 设置同步回调
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        // 设置异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        // 参数
        // 声明一个map 集合来存储参数
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no", paymentInfo.getOutTradeNo());
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", paymentInfo.getTotalAmount());
        map.put("subject", paymentInfo.getSubject());
        // 将封装好的参数传递给支付宝！
        alipayRequest.setBizContent(JSON.toJSONString(map));

        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");
        return form;
    }

    /**
     * 同步回调，重定向到支付模块
     */
    @RequestMapping("alipay/callback/return")
    public String callbackReturn() {
        return "redirect:" + AlipayConfig.return_order_url;
    }

    /**
     * 异步回调，确认已经付款，通知电商模块
     * <p>
     * 1、验证回调信息的真伪
     * 2、验证用户付款的成功与否
     * 3、把新的支付状态写入支付信息表paymentinfo
     * 4、通知电商
     * 5、给支付宝返回回执
     */
    @RequestMapping("alipay/callback/notify")
    @ResponseBody
    public String callbackNotify(@RequestParam Map<String, String> paramMap, HttpServletRequest request) {

        //将异步通知中收到的所有参数都存放到map中
        boolean flag = false; //调用SDK验证签名
        try {
            flag = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        if (flag) {
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            // 对业务的二次校验
            // 只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功。
            // 支付成功之后，需要做什么？
            // 需要得到trade_status
            String trade_status = paramMap.get("trade_status");
            // 通过out_trade_no 查询支付状态记录
            String out_trade_no = paramMap.get("out_trade_no");

            // String total_amount = paramMap.get("total_amount");
            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
                // 当前的订单支付状态如果是已经付款，或者是关闭
                // select * from paymentInfo where out_trade_no =?
                PaymentInfo paymentInfoQuery = new PaymentInfo();
                paymentInfoQuery.setOutTradeNo(out_trade_no);
                PaymentInfo paymentInfo = paymentService.getPaymentInfo(paymentInfoQuery);

                if (paymentInfo.getPaymentStatus() == PaymentStatus.PAID || paymentInfo.getPaymentStatus() == PaymentStatus.ClOSED) {
                    return "failure";
                }

                // 更新交易记录的状态！
                // update paymentInfo set PaymentStatus=PaymentStatus.PAID , callbackTime = new Date() where out_trade_no=?

                PaymentInfo paymentInfoUPD = new PaymentInfo();
                paymentInfoUPD.setPaymentStatus(PaymentStatus.PAID);
                paymentInfoUPD.setCallbackTime(new Date());

                paymentService.updatePaymentInfo(out_trade_no, paymentInfoUPD);
                return "success";
            }
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }
        return "failure";
    }

    /**
     * 退款
     */
    // http://payment.gmall.com/refund?orderId=100
    @RequestMapping("refund")
    @ResponseBody
    public String refund(String orderId) {
        // 退款接口
        boolean result = paymentService.refund(orderId);
        return "" + result;
    }

}
