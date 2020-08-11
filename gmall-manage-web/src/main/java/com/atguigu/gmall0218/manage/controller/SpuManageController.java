package com.atguigu.gmall0218.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0218.bean.SpuInfo;
import com.atguigu.gmall0218.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 任青成
 * @date 2020/8/11 21:32
 */
@RestController
@CrossOrigin
public class SpuManageController {

    @Reference
    private ManageService manageService;

    @RequestMapping("spuList")
    public List<SpuInfo> spuList(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        List<SpuInfo> spuInfoList = manageService.getSpuInfoList(spuInfo);
        return spuInfoList;
    }

    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo) {

        if (spuInfo != null) {
            manageService.saveSpuInfo(spuInfo);
        }

    }

}
