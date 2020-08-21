package com.chen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chen.gmall.bean.PmsBaseAttrInfo;
import com.chen.gmall.bean.PmsBaseAttrValue;
import com.chen.gmall.bean.PmsBaseSaleAttr;
import com.chen.gmall.manage.mapper.AttrInfoMapper;
import com.chen.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.chen.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.chen.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.chen.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    private AttrInfoMapper attrInfoMapper;

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;



    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = attrInfoMapper.select(pmsBaseAttrInfo);

        for (PmsBaseAttrInfo baseAttrInfo : pmsBaseAttrInfoList) {
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            baseAttrInfo.setAttrValueList(pmsBaseAttrValueList);
        }

        return pmsBaseAttrInfoList;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);

        List<PmsBaseAttrValue> attrInfoList= pmsBaseAttrInfo.getAttrValueList();
        for (PmsBaseAttrValue pmsBaseAttrValue : attrInfoList) {
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());

            pmsBaseAttrValueMapper.insertSelective(pmsBaseAttrValue);
        }
        return "success";
    }

    @Override
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {

        List<PmsBaseSaleAttr> baseSaleAttrs =  pmsBaseSaleAttrMapper.selectAll();

        return baseSaleAttrs;
    }
}
