package com.atguigu.gmall0218.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author 任青成
 * @date 2020/8/11 21:24
 */
@Data
public class BaseSaleAttr implements Serializable {

    @Id
    @Column
    private String id;
    @Column
    private String name;

}
