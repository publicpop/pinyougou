app.controller('baseController', function ($scope) {
    //重新加载列表 数据
    $scope.reloadList = function () {//封装切换页码数据
        //切换页码
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,//初始化每页显示数据的数量
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {//自动加载当前页码及数据
            $scope.reloadList();
        }
    };

    $scope.selectIds = [];//作用域新建一个数组空间
    //重选
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//如果选中
            $scope.selectIds.push(id);//+1
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1); //-1
        }

    };



    //提取json字串的某个属性,返回拼接字串\
    $scope.jsonToString = function (jsonStirng, key) {
        //将传入的的字串封装成json对象
        var json = JSON.parse(jsonStirng);
        //定义value用于接收json对象
        value = "";
        //遍历json并取出对应的值拼接成字串
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ",";//!!!!
            }
            value += json[i][key];
        }
        return value;
    }

});