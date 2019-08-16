//控制层
app.controller('goodsController', function ($scope, $controller,$location,typeTemplateService,itemCatService, goodsService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //定义页面实体结构类
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}, itemList: {}};


    //查询实体
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                //商品基本信息
                $scope.entity = response;
                $scope.entity.goods.isEnableSpec = 1;

                //商品富文本信息
                editor.html($scope.entity.goodsDesc.introduction);
                // 商品图片信息
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //商品扩展属性信息
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //商品规格
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //商品SKU信息
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }else {
                    alert(response.message);
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    //商品状态
    $scope.status = ["未审核", "审核通过", "审核未通过", "已关闭"];

    $scope.itemCatList = [];

    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            });
    }

    //一级分类下拉列表
    $scope.selectItemCat1List = function () {

        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    }

    //二级分类下拉列表
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
            }
        )
    })

    //三级下拉列表
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        )
    })

    //模板id
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        )
    })

    //通过模板di获取品牌下拉列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                if ($location.search()['id'] == null) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                }
            }
        );
        // typeTemplateService.findSpecList(newValue).success(
        //     function (response) {
        //         $scope.specList = response;
        //     }
        // )
    })


    //添加选中的规格属性
    $scope.updateSpec = function ($event, name, value) {
        //获取option集合
        var object = $scope.searchObjByKey(
            $scope.entity.goodsDesc.specificationItems, 'attributeName', name
        );
        //如果集合存在
        if (object != null) {
            //如果详情选项被点击,则向集合obj集合中添加元素//当取消勾选时,删除此元素
            if ($event.target.checked) {
                object.attributeValue.push(value);
            } else {
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1)
                //如果obj中的元素全部删除时,此集合也将被删除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1)
                }
            }
            //当obj为空时,将创建此集合
        } else {
            $scope.entity.goodsDesc.specificationItems.push({'attributeName': name, 'attributeValue': [value]})
        }
    }


    //创建SKU列表
    $scope.createItemList = function () {
        //初始化SKU列表
        $scope.entity.itemList = [{spec: {}, price: 0, num: 0, status: 'o', isDefault: '0'}];
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList
                , items[i].attributeName, items[i].attributeValue)
        }
    }

    //创建列表对应值//items中的元素集合的乘积为SKU列表的行数
    addColumn = function (list, columnName, columnValues) {
        var newList = [];//新建集合//存放SKU行数据
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }


    //审核商品
    $scope.updateStatus = function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectIds = [];
                }else {
                    alert(response.message);
                }
            }
        )
    }
});	
