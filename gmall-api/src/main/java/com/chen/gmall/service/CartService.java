package com.chen.gmall.service;

import com.chen.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {
    OmsCartItem ifCarExistByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);

    List<OmsCartItem> cartList(String userId);
}
