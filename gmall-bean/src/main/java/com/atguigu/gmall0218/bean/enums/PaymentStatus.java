package com.atguigu.gmall0218.bean.enums;

public enum PaymentStatus {
    UNPAID("支付中"),
    PAID("已支付"),
    PAY_FAIL("支付失败"),
    ClOSED("已关闭");

    private final String name;

    PaymentStatus(String name) {
        this.name = name;
    }

}
