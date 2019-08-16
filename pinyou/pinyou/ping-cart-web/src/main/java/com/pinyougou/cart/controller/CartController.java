package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.util.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/findCartList")
    /**
     * 获取cookie中的购物车
     * @param
     * @Return:java.util.List<com.pinyougou.pojogroup.Cart>
     */
    public List<Cart> findCartList() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果用户是anonymousUser,从cookie中取出购物车,否则,从redis中取出购物车
        //获取cookie
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //如果此cookie不存在
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        //将提取到的cookie字串格式为list集合
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        //返回cookie中获取到的集合
        if (name.equals("anonymousUser")) {
            return cartList_cookie;
        } else {
            //redis中获取购物车列表
            List<Cart> cartList = cartService.findCartListByRedis(name);
            //如果cookie中的购物车的信息不为空,则将其信息没添加至redis购物车集合中
            if (cartList_cookie.size() > 0) {
                cartList = cartService.mergeList(cartList_cookie, cartList);
            }
            return cartList;
        }
    }


    @RequestMapping("/addGoodsToCartList")
    /**
     * 将用户的购物车信息存于cookie中
     * @param itemId 商品SKU  ID
     * @param num   商品数量
     * @Return:entity.Result
     */
    @CrossOrigin(origins = "http://www.abc.com:9005",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) {

        //跨域解决方案
//        response.setHeader("Access-Control-Allow-Origin","http://www.abc.com:9005");
//        response.setHeader("Access-Control-Allow-Credentials","true");


        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //获取cookie中的购物车信息
            List<Cart> cartList = findCartList();
            //核对购物车中的信息
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (name == "anonymousUser") {
                //将核对后的购物车信息添加至cookie
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
            } else {
                //将用户购物车存入redis中
                cartService.saveCartListByRedis(name, cartList);
            }
            return new Result(true, "添加成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "添加失败");
        }
    }
}
