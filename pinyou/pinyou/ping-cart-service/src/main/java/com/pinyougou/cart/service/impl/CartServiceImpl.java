package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    /**
     * 商品添加购物车
     * @param castlist
     * @param itemId
     * @param num
     * @Return:java.util.List<com.pinyougou.pojogroup.Cart>
     */
    public List<Cart> addGoodsToCartList(List<Cart> castlist, Long itemId, Integer num) {

        //查询商品的SKU信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //判断商品是否满足出售条件
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");
        }

        //获取商家id
        String sellerId = item.getSellerId();
        //商家购物车对象
        Cart cartObj = null;
        //根据商家id查询商家购物车对象
        for (Cart cart : castlist) {
            if (cart.getSellerId().equals(sellerId)) {
                cartObj = cart;
            }
        }
        //购物车列表中是否存在对应的商家购物车
        if (cartObj == null) {
            //创建商家购物车
            cartObj = new Cart();
            cartObj.setSellerId(sellerId);
            cartObj.setSellerName(item.getSeller());
            TbOrderItem orderItem = createCart(item, num);

            //创建订单信息集合
            List orderItemList = new ArrayList();
            orderItemList.add(orderItem);
            //将订单集合添加至商家购物车中
            cartObj.setOrderItemList(orderItemList);
            //将商家购物车添加至用户购物车
            castlist.add(cartObj);
        } else {
            //查询TbOrderItem是否存在于购物车中
            TbOrderItem orderItemObj = null;
            for (TbOrderItem orderItem : cartObj.getOrderItemList()) {
                if (orderItem.getItemId().longValue() == itemId.longValue()) {
                    orderItemObj = orderItem;
                }
            }
            //没有则创建
            if (orderItemObj == null) {
                orderItemObj = createCart(item, num);
                cartObj.getOrderItemList().add(orderItemObj);
            } else {
                //存在则在其原有的数量的基础上加上numbing修改最终金额
                orderItemObj.setNum(orderItemObj.getNum() + num);
                orderItemObj.setTotalFee(new BigDecimal(orderItemObj.getNum() * orderItemObj.getPrice().doubleValue()));
                //明细数量小于0时,商品移除商家购物车
                if (orderItemObj.getNum() <= 0) {
                    cartObj.getOrderItemList().remove(orderItemObj);
                }
                //如果移除后商家购物车,购物车的数量为0,则将此购物车移除用户购物车
                if (cartObj.getOrderItemList().size() == 0) {
                    castlist.remove(cartObj);
                }
            }
        }


        return castlist;
    }

    /**
     * 创建订单详情对象
     *
     * @param item
     * @param num
     * @Return:
     */
    private TbOrderItem createCart(TbItem item, Integer num) {

        //购买数量不得小于0
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        //创建订单详情并添加相应的信息
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    /**
     * 根据用户信息,在redis中查询其对应的购物车信息
     * @param username
     * @Return:java.util.List<com.pinyougou.pojogroup.Cart>
     */
    public List<Cart> findCartListByRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cart").get(username);
        //如果用户的购物车在redis中不存在,则新建购物车
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    /**
     * 将用户的购物车信息,以用户名为key,购物车信息为value保存到redis中
     * @param name
     * @param cartList
     * @Return:void
     */
    public void saveCartListByRedis(String name, List<Cart> cartList) {

        redisTemplate.boundHashOps("cart").put(name, cartList);

    }


    @Override
    /**
     * 合并cookie与redis中的购物车信息
     * @param list1  cookie购物车信息
     * @param list2  redis中购物车信息
     * @Return:java.util.List<com.pinyougou.pojogroup.Cart>
     */
    public List<Cart> mergeList(List<Cart> list1, List<Cart> list2) {
        //将cookie中的信息添加至登录用户的redis购物车信息中
        for (Cart cart : list1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                list2 = addGoodsToCartList(list2, orderItem.getItemId(), orderItem.getNum());
            }
        }

        return list2;
    }
}
