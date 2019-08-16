//控制层
app.controller('itemCatController', function ($scope, $controller, typeTemplateService, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            $scope.entity.parentId = $scope.parentId;
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.findByParentId($scope.parentId);//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(

            function (response) {
                if (response.success) {
                    $scope.findByParentId($scope.parentId);//刷新列表
                }else {
                    alert(response.message);
                }
            }
        );
    };

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //定义父级编号并初始化
    $scope.parentId = 0;
    //商品分级查询
    $scope.findByParentId = function (id) {
        //将传入的id记录赋值给$scope.parentId
        $scope.parentId = id;
        itemCatService.findByParentId(id).success(
            function (response) {
                $scope.list = response;
            });
    }

    //设置面包屑初始级别
    $scope.grade = 1;
    //设置面包屑分级级别
    $scope.setGrade = function (value) {
        $scope.grade = value;
    }
    //获取分级列表
    $scope.getList = function (p_entity) {
        $scope.selectIds = [];
        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 2) {
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 3) {
            $scope.entity_2 = p_entity;
        }
        $scope.findByParentId(p_entity.id);

    }

    //查询模板名及id
    //			未定义时优先检查!!!!data!!***
    $scope.typeList = {data: []};
    $scope.findOptionList = function () {
        //模板数据
        typeTemplateService.selectTypeList().success(
            function (response) {
                $scope.typeList = {data: response};
            });

    }

});	
