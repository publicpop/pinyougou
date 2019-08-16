app.controller('itemController', function ($scope, $http) {

    //数控操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        //添加数量不得小于1
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }

    //规格集合
    $scope.specificationItems = {};

    //规格选择
    $scope.selectSpec = function (name, value) {
        $scope.specificationItems[name] = value;
        searchSku();
    }

    //判断规格选项是否选择
    $scope.isSelect = function (name, value) {
        if ($scope.specificationItems[name] == value) {
            return true;
        } else {
            return false;
        }
    }

    //定义sku变量
    $scope.sku = {};

    //加载SKU信息
    $scope.loadSKU = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //匹配两个对象
    matchObject = function (map1, map2) {
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        for (var k in map2) {
            if (map2[k] != map1[k]) {
                return false;
            }
        }
        return true;
    }

    //查询SKU
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        //没有相应的匹配
        $scope.sku = {id: 0, title: '--------', price: '--'};
    }

    //添加商品到购物车
    $scope.addToCart = function () {
        $http.get('http://www.abc.com:9007/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id + "&num=" + $scope.num,{'withCredentials':true}).success(
            function (response) {
                if(response){
                    location.href = "http://www.abc.com:9007/cart.html"
                }else {
                    alert(response.message);
                }
            }
        )
    }

})