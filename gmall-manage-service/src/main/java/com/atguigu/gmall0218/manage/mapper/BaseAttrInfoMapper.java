package com.atguigu.gmall0218.manage.mapper;

import com.atguigu.gmall0218.bean.BaseAttrInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author rcn
 * @date 2020/8/8 21:48
 */
public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {

    /**
     * description: 根据三级分类id查询平台属性
     */
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(String catalog3Id);

}
