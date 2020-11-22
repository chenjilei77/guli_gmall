package com.chen.gmall.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.chen.gmall.annotations.LoginRequired;
import com.chen.gmall.bean.OmsCartItem;
import com.chen.gmall.bean.PmsSkuInfo;
import com.chen.gmall.service.CartService;
import com.chen.gmall.service.SkuService;
import com.chen.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {
    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

    @RequestMapping("toTrade")
    @LoginRequired(loginSuccess = false)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap map){
        String memberId = (String) request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickName");


        return "toTrade";
    }

    @RequestMapping("index")
    public String index(){
        System.out.println("ssssssss1");
        System.out.println("ssssssss2");
        System.out.println("ssssssss3");
        return null;
    }
    @RequestMapping("cartList")
    @LoginRequired(loginSuccess = true)
    public String cartList(HttpServletRequest request, HttpServletResponse response, ModelMap map){

        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = (String) request.getAttribute("memberId");
        if(StringUtils.isNotBlank(memberId)){
            //如何已经登陆，查询db
            omsCartItems=cartService.cartList(memberId);
        }else{
            //如果没有登陆，查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }
        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice());
        }

        map.put("cartList",omsCartItems);

        return "cartList";
    }

    @RequestMapping("addToCart")
    @LoginRequired(loginSuccess = false)
    public String addToCart(String skuId, long quantity, HttpServletRequest request, HttpServletResponse response, HttpSession session){

        //调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId);

        //将商品信息封装成购物车信息
        //判断用户是否登陆
        String memberId = (String) request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickName");
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setMemberNickname(nickName);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductBrand("");
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setQuantity(12);
        omsCartItem.setProductSn("");
        omsCartItem.setProductPic("");
        omsCartItem.setProductSkuCode("");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setProductSubTitle("");

            if(StringUtils.isBlank(memberId)){
            //用户没有登陆
            List<OmsCartItem> omsCartItems = new ArrayList<>();
            //cookie中原有数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookie)){
                //cookie为空
                omsCartItems.add(omsCartItem);
            }else {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                //判断添加的购物车数据在cookie中是否存在
                boolean exist = if_cart_exist(omsCartItems,omsCartItem);

                if(exist){
                    //之前添加过，更新购物车数量
                    for (OmsCartItem cartItem : omsCartItems) {
                        if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            cartItem.setQuantity(cartItem.getQuantity()+omsCartItem.getQuantity());
                            cartItem.setPrice(cartItem.getPrice().add(omsCartItem.getPrice()));
                        }
                    }
                }else {
                    //没有添加过，将购物车数据添加
                    omsCartItems.add(omsCartItem);
                }
            }


            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(omsCartItems),60*60*72,true);
        }else {
            //用户已经登陆
            List<OmsCartItem> omsCartItems = new ArrayList<>();
            OmsCartItem omsCartItemFromDb = cartService.ifCarExistByUser(memberId,skuId);

            if(omsCartItemFromDb==null){
                //db为空，该用户没有添加过当前商品
                omsCartItem.setMemberId(memberId);
                cartService.addCart(omsCartItem);
            }else {
                    //之前添加过
                omsCartItemFromDb.setQuantity(omsCartItem.getQuantity()+omsCartItemFromDb.getQuantity());
                cartService.updateCart(omsCartItemFromDb);
            }

            cartService.flushCartCache(memberId);

        }
        //

        return "redirect:success.html";
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean result = false;
        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();
            if(productSkuId.equals(omsCartItem.getProductSkuId())){
                result = true;
            }
        }

        return result;
    }


}
