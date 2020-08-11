package com.atguigu.gmall0218.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author 任青成
 * @date 2020/8/11 22:28
 */
@Data
public class SpuSaleAttrValue implements Serializable {

    @Id
    @Column
    String id;

    @Column
    String spuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrValueName;
    // isChecked 什么用？ 当前的属性值是否被选中！
    @Transient
    String isChecked;


}
