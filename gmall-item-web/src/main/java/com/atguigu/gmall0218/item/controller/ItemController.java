package com.atguigu.gmall0218.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0218.bean.SkuInfo;
import com.atguigu.gmall0218.bean.SkuSaleAttrValue;
import com.atguigu.gmall0218.bean.SpuSaleAttr;
import com.atguigu.gmall0218.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/13 15:42
 */

@Controller
public class ItemController {


    @Reference
    private ManageService manageService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, HttpServletRequest requestRequest) {
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        requestRequest.setAttribute("skuInfo", skuInfo);
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        requestRequest.setAttribute("spuSaleAttrList", spuSaleAttrList);

        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        String key = "";
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if (key.length() > 0) {
                key += "|";
            }
            key += skuSaleAttrValue.getSaleAttrValueId();
            if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i + 1).getSkuId())) {
                // 放入map集合
                map.put(key, skuSaleAttrValue.getSkuId());
                // 并且清空key
                key = "";
            }
        }
        // 将map 转换为json 字符串
        String valuesSkuJson = JSON.toJSONString(map);
        // 保存json
        requestRequest.setAttribute("valuesSkuJson", valuesSkuJson);

        return "item";
    }


}
