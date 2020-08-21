package com.chen.gmall.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chen.gmall.bean.PmsProductSaleAttr;
import com.chen.gmall.bean.PmsSkuInfo;
import com.chen.gmall.service.SkuService;
import com.chen.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap mv){
        PmsSkuInfo pmsSkuInfo =  skuService.getSkuById(skuId);

        //sku对象
        mv.put("skuInfo",pmsSkuInfo);
        //销售属性列表

        List<PmsProductSaleAttr>  pmsProductSaleAttrs=spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),skuId);
        mv.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);


        return "item";
    }

}
