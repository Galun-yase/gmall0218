package com.atguigu.gmall0218.manage.mapper;

import com.atguigu.gmall0218.bean.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/12 21:25
 */
public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {

    List<SkuSaleAttrValue> selectSkuSaleAttrValueListBySpu(@Param("spuId") String spuId);

}
