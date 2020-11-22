package com.chen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.chen.gmall.bean.PmsSkuAttrValue;
import com.chen.gmall.bean.PmsSkuImage;
import com.chen.gmall.bean.PmsSkuInfo;
import com.chen.gmall.bean.PmsSkuSaleAttrValue;
import com.chen.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.chen.gmall.manage.mapper.PmsSkuImageMapper;
import com.chen.gmall.manage.mapper.PmsSkuInfoMapper;
import com.chen.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.chen.gmall.service.SkuService;
import com.chen.gmall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;



    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if(StringUtils.isBlank(skuDefaultImg)){
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }


        List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuAttrValueList) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        List<PmsSkuImage> pmsSkuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : pmsSkuImageList) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId){

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();

        //连接缓存
        Jedis jedis = redisUtil.getJedis();
        //查询缓存
        String skuKey = "sku:"+skuId+":info";
        String skuJson = jedis.get(skuKey);
        if(StringUtils.isNotBlank(skuJson)){
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        }else {
            //如果缓存中没有，再查询数据库
            //设置分布式锁
            String token = UUID.randomUUID().toString();
            String Ok = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10);
            if(StringUtils.isNotBlank(Ok)){
                pmsSkuInfo = getSkuByIdFromDb(skuId);

                if(pmsSkuInfo!=null){
                    jedis.set( "sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                }else {
                    jedis.setex("sku:"+skuId+":info",60*3,JSON.toJSONString(""));
                }
                String lockToken = jedis.get("sku:" + skuId + ":lock");
                if (StringUtils.isNotBlank(lockToken) && lockToken.equals(token)){
                    jedis.del( "sku:"+skuId+":lock");//确定删除的是自己的锁
                }

            }else {
                //设置失败，自旋（该线程在沉睡几秒后，重新访问本方法）
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuById(skuId);
            }

        }

        jedis.close();

        return pmsSkuInfo;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal price) {

        boolean b = false;

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        BigDecimal productPrice = pmsSkuInfo1.getPrice();

        if(price.compareTo(productPrice)==0){
            b = true;
        }


        return b;
    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId) {

        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        //sku的图片
       PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        pmsSkuInfo1.setSkuImageList(pmsSkuImages);
        return pmsSkuInfo1;
    }
}
