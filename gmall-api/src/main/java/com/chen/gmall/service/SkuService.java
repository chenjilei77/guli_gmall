package com.chen.gmall.service;

import com.chen.gmall.bean.PmsSkuInfo;

import java.math.BigDecimal;

public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);

    boolean checkPrice(String productSkuId, BigDecimal price);
}
