app.service('seckillService',function ($http) {

    //查询通过审核的秒杀商品
    this.findList = function () {
        return $http.get('/seckillGoods/findList.do');
    }

    //更具id在redis查询秒杀商品详情
    this.findByIdByRedis = function (id) {
        return $http.get('/seckillGoods/findByIdByRedis.do?id='+id)
    }

    //提交订单
    this.submitOrder = function (secskillId) {
        return $http.get('/seckillOrder/submitOrder.do?seckillId='+secskillId)
    }
})