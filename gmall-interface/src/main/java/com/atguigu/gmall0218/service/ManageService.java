package com.atguigu.gmall0218.service;

import com.atguigu.gmall0218.bean.BaseAttrInfo;
import com.atguigu.gmall0218.bean.BaseAttrValue;
import com.atguigu.gmall0218.bean.BaseCatalog1;
import com.atguigu.gmall0218.bean.BaseCatalog2;
import com.atguigu.gmall0218.bean.BaseCatalog3;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author rcn
 * @date 2020/8/8 22:29
 */
public interface ManageService {

    /**
     * description:获取所有的一级分类数据
     *
     * @param
     * @return java.util.List<com.atguigu.gmall0218.bean.BaseCatalog1>
     */
    List<BaseCatalog1> getCatalog1();

    /**
     * description: 根据一级分类Id,获取所有的二级分类数据
     *
     * @param catalog1Id
     * @return java.util.List<com.atguigu.gmall0218.bean.BaseCatalog2>
     */
    List<BaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * description:根据二级分类Id,获取所有的三级分类数据
     *
     * @param catalog2Id
     * @return java.util.List<com.atguigu.gmall0218.bean.BaseCatalog3>
     */
    List<BaseCatalog3> getCatalog3(String catalog2Id);

    /**
     * description: 根据三级分类Id，查询平台属性集合
     */
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    /**
     * description: 根据平台属性Id,查询平台属性值集合
     */
    List<BaseAttrValue> getAttrValueList(String attrId);

    /**
     * description: 保存平台属性数据
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * description: 根据平台属性Id,查询平台属性对象
     */
    BaseAttrInfo getAttrInfo(String attrId);

}
