package com.atguigu.gmall0218.manage.mapper;

import com.atguigu.gmall0218.bean.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    /**
     * description: 根据spuId查询销售属性集合
     */
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

    /**
     * description: 根据spuId,查询销售属性，销售属性值，
     * 以及根据skuId查询被锁定的值
     */
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("spuId") String spuId, @Param("skuId") String skuId);


}
