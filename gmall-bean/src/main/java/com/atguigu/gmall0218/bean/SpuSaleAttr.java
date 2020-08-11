package com.atguigu.gmall0218.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/11 22:24
 */
@Data
public class SpuSaleAttr implements Serializable {

    // 销售属性值集合
    @Transient
    List<SpuSaleAttrValue> spuSaleAttrValueList;
    @Id
    @Column
    private String id;
    @Column
    private String spuId;
    @Column
    private String saleAttrId;
    @Column
    private String saleAttrName;


}
