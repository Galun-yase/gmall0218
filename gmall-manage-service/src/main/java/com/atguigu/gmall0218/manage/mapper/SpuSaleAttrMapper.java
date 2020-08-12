package com.atguigu.gmall0218.manage.mapper;

import com.atguigu.gmall0218.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    /**
     * description: 根据spuId查询销售属性集合
     */
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

}
