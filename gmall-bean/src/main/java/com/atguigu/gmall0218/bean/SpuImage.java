package com.atguigu.gmall0218.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author 任青成
 * @date 2020/8/11 22:21
 */
@Data
public class SpuImage implements Serializable {

    @Id
    @Column
    private String id;
    @Column
    private String spuId;
    @Column
    private String imgName;
    @Column
    private String imgUrl;

}
