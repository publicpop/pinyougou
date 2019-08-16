package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
    * 商品添加购物车
    * @param castlist 购物车
    * @param itemId 商品SKUid
    * @param num 商品数量
    * @Return:
    */
    List<Cart> addGoodsToCartList(List<Cart> castlist,Long itemId,Integer num);


    /**
    * 根据用户信息,在redis中查询其对应的购物车信息
    * @param username
    * @Return:
    */
    List<Cart> findCartListByRedis(String username);


    /**
    * 将用户的购物车信息,以用户名为key,购物车信息为value保存到redis中
    * @param cartList
    * @Return:
    */
    void saveCartListByRedis(String name,List<Cart> cartList);

    /**
    * 合并cookie与redis中的购物车信息
    * @param list1  cookie购物车信息
    * @param list2  redis购物车信息
    * @Return:
    */
    List<Cart> mergeList(List<Cart> list1,List<Cart> list2);

}
