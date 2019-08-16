app.controller('payController',function ($scope,payService) {


    //本地支付
    $scope.createNative = function () {
        payService.createNative().success(
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
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if(response){
                    location.href="paysuccess.html#?money="+$scope.money;
                }else {
                    if(response.message=='二维码超时'){
                        location.href = "payTimeOut.html";
                    }else {
                        location.href = "payfail.html";
                    }
                }
            }
        )
    }


})