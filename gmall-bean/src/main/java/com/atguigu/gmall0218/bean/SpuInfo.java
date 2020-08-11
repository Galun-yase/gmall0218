package com.atguigu.gmall0218.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/11 13:44
 */
@Data
public class SpuInfo implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String spuName;
    @Column
    private String description;
    @Column
    private String catalog3Id;

    // 销售属性集合
    @Transient
    private List<SpuSaleAttr> spuSaleAttrList;
    // 图片列表集合
    @Transient
    private List<SpuImage> spuImageList;


}
