package com.atguigu.gmall0218.service;

import com.atguigu.gmall0218.bean.BaseAttrInfo;
import com.atguigu.gmall0218.bean.BaseAttrValue;
import com.atguigu.gmall0218.bean.BaseCatalog1;
import com.atguigu.gmall0218.bean.BaseCatalog2;
import com.atguigu.gmall0218.bean.BaseCatalog3;
import com.atguigu.gmall0218.bean.BaseSaleAttr;
import com.atguigu.gmall0218.bean.SkuImage;
import com.atguigu.gmall0218.bean.SkuInfo;
import com.atguigu.gmall0218.bean.SkuSaleAttrValue;
import com.atguigu.gmall0218.bean.SpuImage;
import com.atguigu.gmall0218.bean.SpuInfo;
import com.atguigu.gmall0218.bean.SpuSaleAttr;

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

    /**
     * description: 根据spuinfo信息查询spuinfo列表
     */
    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    /**
     * 获取所有的销售属性数据
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * description: 获取spuImage
     */
    List<SpuImage> getSpuImageList(SpuImage spuImage);

    /**
     * description: 获取spu销售属性集合
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    /**
     * description: 保存skuInfo数据
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * description: 根据skuId查询skuInfo
     */
    SkuInfo getSkuInfo(String skuId);

    /**
     * description: 根据skuId查询skuImage
     */
    List<SkuImage> getSkuImageBySkuId(String skuId);

    /**
     * description: 获取销售属性及值，并获取选中状态
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /**
     * description: 根据spuId，获取sku销售属性的id，skuid
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);
}
