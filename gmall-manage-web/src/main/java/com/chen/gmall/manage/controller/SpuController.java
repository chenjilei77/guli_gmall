package com.chen.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.chen.gmall.bean.PmsProductImage;
import com.chen.gmall.bean.PmsProductInfo;
import com.chen.gmall.bean.PmsProductSaleAttr;
import com.chen.gmall.manage.util.PmsUploadUtil;
import com.chen.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin
public class SpuController {

    @Reference
    private SpuService spuService;

    @RequestMapping("fileUpload")
    @ResponseBody
    public String
    fileUpload(@RequestParam("file") MultipartFile file){
        //将图片或者音频视频上传到分布式的文件存储系统中
        //将图片的路径发给前端
        String imgUrl = PmsUploadUtil.uploadImage(file);

        return imgUrl;
    }

    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> getSpuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfos = spuService.getSpuList(catalog3Id);
        return pmsProductInfos;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> getPmsProductSaleAttrList(@RequestParam("spuId") String productId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList =  spuService.getPmsProductSaleAttrList(productId);
        return pmsProductSaleAttrList;
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> getPmsProductImageList(@RequestParam("spuId") String productId){
        List<PmsProductImage> pmsProductImages =  spuService.getPmsProductImageList(productId);
        return pmsProductImages;
    }
}
