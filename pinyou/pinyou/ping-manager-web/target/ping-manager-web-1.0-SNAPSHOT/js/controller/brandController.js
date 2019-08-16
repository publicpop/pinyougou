// 作用域    请求协议(通过其获取请求方式)
app.controller('brandController', function ($scope, $controller,brandService) {

    $controller('baseController', {$scope: $scope});//继承baseController//域共享

    //品牌查询
    $scope.findAll = function () {
        //           请求路径            回调函数
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    // //重新加载列表 数据
    // $scope.reloadList = function () {//封装切换页码数据
    //     //切换页码
    //     $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    // };
    // //分页控件配置
    // $scope.paginationConf = {
    //     currentPage: 1,
    //     totalItems: 10,
    //     itemsPerPage: 10,//初始化每页显示数据的数量
    //     perPageOptions: [10, 20, 30, 40, 50],
    //     onChange: function () {//自动加载当前页码及数据
    //         $scope.reloadList();
    //     }
    // };

    $scope.searchentity = {};
    //条件查询
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchentity).success(
            function (response) {
                $scope.list = response.rows;//当前页码展示数据
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //添加品牌数据
    $scope.save = function () {
        var obj = null;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }

        obj.success(function (response) {
            if (response.success) {
                //如果添加成功则刷新当前页更改项数据
                $scope.reloadList();
            } else {
                //如果添加不成功,则弹出相应信息
                alert(response.message);
            }
        })
    };

    //查询数据
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    };


    // $scope.selectIds = [];//作用于新建一个数组空间
    // //重选
    // $scope.updateSelection = function ($event, id) {
    //     if($event.target.checked){//如果选中
    //         $scope.selectIds.push(id);
    //     }else {
    //         var idx = $scope.selectIds.indexOf(id);
    //         $scope.selectIds.splice(idx,1);
    //     }
    // };

    //批量删除
    $scope.delete = function () {
        //                          数据键值对的key必须与后台的所获取的key一致
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            })
    }

});
