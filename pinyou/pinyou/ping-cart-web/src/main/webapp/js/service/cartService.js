app.service('cartService', function ($http) {
    //查找cookie中的购物车
    this.findCartList = function () {
        return $http.get('cart/findCartList.do')
    }

    //购物车商品的添加和删除
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('/cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);
    }


    this.sum = function (cartList) {
        var totalValue = {totaleNum: 0, totalePrice: 0.00};
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i].orderItemList;
            for (var j = 0; j < cart.length; j++) {
                totalValue.totaleNum += cart[j].num;
                totalValue.totalePrice += cart[j].totalFee;
            }
        }
        return totalValue;
    }

    //查询用户收货地址
    this.findAddressList = function () {
        return $http.get('/address/findAddressByName.do');
    }

    //添加收货人地址
    this.addByAddress = function (entity) {
        return $http.post('/address/add.do',entity)
    }


    //保存订单
    this.submitOrder = function (order) {
        return $http.post('/order/add.do',order);
    }

    //本地支付
    this.createNative = function () {
        return $http.get('/pay/createNative.do')
    }

    //查询支付状态
    this.queryPayStatus = function () {
        return $http.get('/pay/queryPayStatus.do?out_trade_no='+out_trade_no)
    }

})