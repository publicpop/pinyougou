app.controller('seckillController', function ($scope, $location, $interval, seckillService) {


    //查询通过审核的商品
    $scope.findList = function () {
        seckillService.findList().success(
            function (response) {
                $scope.seckillList = response;
            }
        )
    }

    //更具id在redis查询秒杀商品详情
    $scope.findByIdByRedis = function () {
        seckillService.findByIdByRedis($location.search()['id']).success(
            function (response) {
                $scope.seckillGoods = response;
                //秒杀倒计时总秒数
                allsecond = Math.floor((new Date($scope.seckillGoods.endTime).getTime() - (new Date().getTime())) / 1000);
                time = $interval(
                    function () {
                        if (allsecond > 0) {
                            allsecond = allsecond - 1;
                            $scope.timeStrng = converTime(allsecond);
                        } else {
                            $interval.cancel(time);
                            alert("秒杀活动已结束")
                        }
                    }, 1000
                )
            }
        )
    }

    converTime = function (allsecond) {
        var days = Math.floor(allsecond / (60 * 60 * 24))//天数
        var hours = Math.floor((allsecond - days * 60 * 60 * 24) / (60 * 60))//小时
        var minutes = Math.floor((allsecond - days * 60 * 60 * 24 - hours * 60 * 60) / 60)//分钟数
        var seconds = allsecond - days * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60 //秒数

        timeStrng = "";

        if (days > 0) {
            timeStrng += days + "天  "
        }
        return timeStrng + hours + ":" + minutes + ":" + seconds;
    }

    //提交订单
    $scope.submitorder = function () {
        seckillService.submitOrder($scope.seckillGoods.id).success(
            function (response) {
                if(response.success){
                    alert("请在1分钟内完成支付")
                    location.href="pay.html"
                }else {
                    alert(response.message)
                }
            }
        )
    }

})