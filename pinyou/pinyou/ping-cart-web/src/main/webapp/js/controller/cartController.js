app.controller('cartController', function ($scope,$location, cartService) {

    //查找cookie中的购物车
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);
        })
    }

    //购物车商品的添加和删除
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();
                } else {
                    alert(response.message)
                }
            }
        )
    }

    //查询用户收货地址
    $scope.findAddress = function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList = response;
                for(var i=0;i<response.length;i++){
                    if(response[i].isDefault=="1"){
                        $scope.addres = response[i];
                        $scope.address = response[i];
                        return;
                    }
                }
            }
        )
    }

    //接收选择地址
    $scope.selectAddr = function (address) {
        $scope.address = address;
    }

    //显示选中地址
    $scope.isSelectAddr = function (address) {
        if($scope.address==address){
            return true;
        }else {
            return false;
        }
    }

    $scope.order = {paymentType:'1'}//默认1微信支付//2货到付款
    //支付类型
    $scope.selectPayType = function (payType) {
        $scope.order.paymentType = payType;
    }

    //新增收货人地址
    $scope.addByAddress =function () {
        cartService.addByAddress($scope.entity).success(
            function (response) {
                if(response.success){
                    $scope.findAddress();
                    $scope.entity = {};
                }else {
                    alert(response.message);
                }
            }
        )
    }

    //保存订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;//地址
        $scope.order.receiverMobile = $scope.address.mobile;//联系电话
        $scope.order.receiver = $scope.address.contact;//收件人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    //页面跳转
                    if($scope.order.paymentType=='1'){
                        location.href = 'pay.html'
                    }else {
                        location.href = 'paysuccess.html'
                    }
                }else {
                    alert(response.message)
                }
            }
        )
    }


    //本地支付
    $scope.createNative = function () {
        cartService.createNative().success(
            function (response) {
                $scope.money = (response.total_fee/100).toFixed(2);//支付金额
                $scope.out_trade_no = response.out_trade_no;//订单号
                //二维码
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:'H',
                    value:'https://www.baidu.com'   //真实校验待改善
                })
                queryPayStatus(response.out_trade_no)//查询状态
            }
        )
    }

    //查询用户支付状态
    queryPayStatus = function () {
        cartService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if(response){
                    location.href="paysuccess.html#?money="+$scope.money;
                }else {
                    if(response.message=='二维码过期'){
                        $scope.createNative();//重置二维码
                    }else {
                        location.href = "payfail.html";
                    }
                }
            }
        )
    }


    $scope.getmoney = function () {
        return $location.search()['money'];
    }

})