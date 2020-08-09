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
 * <p>
 *
 * </p>
 *
 * @author rcn
 * @date 2020/8/8 21:42
 */
@Data
public class BaseAttrInfo implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)//可以获取到自增主键
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;

    @Transient//表示不是表中的字段，是业务相关的字段
    private List<BaseAttrValue> attrValueList;
}
