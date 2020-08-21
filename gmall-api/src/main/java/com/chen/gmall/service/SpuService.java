package com.chen.gmall.service;

import com.chen.gmall.bean.PmsProductImage;
import com.chen.gmall.bean.PmsProductInfo;
import com.chen.gmall.bean.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> getSpuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> getPmsProductSaleAttrList(String productId);

    List<PmsProductImage> getPmsProductImageList(String productId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId);
}
