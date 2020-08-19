package com.atguigu.gware.service;

import com.atguigu.gware.bean.WareInfo;
import com.atguigu.gware.bean.WareOrderTask;
import com.atguigu.gware.bean.WareSku;

import java.util.List;
import java.util.Map;

/**
 * @param
 * @return
 */
public interface GwareService {
    Integer getStockBySkuId(String skuid);

    boolean hasStockBySkuId(String skuid, Integer num);

    List<WareInfo> getWareInfoBySkuid(String skuid);

    void addWareInfo();

    Map<String, List<String>> getWareSkuMap(List<String> skuIdlist);

    void addWareSku(WareSku wareSku);

    void deliveryStock(WareOrderTask taskExample);

    WareOrderTask saveWareOrderTask(WareOrderTask wareOrderTask);

    List<WareOrderTask> checkOrderSplit(WareOrderTask wareOrderTask);

    void lockStock(WareOrderTask wareOrderTask);

    List<WareOrderTask> getWareOrderTaskList(WareOrderTask wareOrderTask);

    List<WareSku> getWareSkuList();

    List<WareInfo> getWareInfoList();
}
