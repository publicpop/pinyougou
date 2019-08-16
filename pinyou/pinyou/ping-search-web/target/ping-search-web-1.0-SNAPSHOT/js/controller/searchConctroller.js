app.controller('searchController', function ($scope,$location, searchService) {

//搜索数据
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                buildPageLabel();
            }
        )
    }


    //搜索队象                            **{}没有值为null**
    $scope.searchMap = {'keywords': '', 'category': '', 'brand': '',
        'spec': {},'price':'','pageNo':1,'pageSize':30,'sortField':'','sort':''};


    //添加搜索选项
    $scope.addSearchMap = function (key, value) {
        //添加搜索选项
        if (key == 'brand' || key == 'category'||key=='price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索
    }

    //删除搜素选项
    $scope.deleSearchMap = function (key) {
        //删除选中的搜索选项
        if (key == 'brand' || key == 'category'||key=='price') {
            $scope.searchMap[key] = '';
        }else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//执行搜索
    }

    //构建分页标签(显示总页数为5页)
    buildPageLabel = function () {
        //新增分页栏
        $scope.pageLabel = [];
        //总页数
        var maxPageNo = $scope.resultMap.totalPages;
        //起始页
        var firstPage = 1;
        //最终页
        var lastPage = maxPageNo;
        $scope.firstDot = true;//开始页之前有点
        $scope.lastDot  = true;//结束页之后有点
        if($scope.resultMap.totalPages>5){//如果总页数大于5
            if($scope.searchMap.pageNo<=3){//如果当前页不大于3
                lastPage = 5;//显示最后页码为5
                $scope.firstDot = false;//前无点
            }else if($scope.searchMap.pageNo>=maxPageNo-2){//如果当前页大于等于总页码-2
                firstPage = maxPageNo-4;//最后5页
                $scope.lastDot  =false;//后无点
            }else {
                firstPage = $scope.searchMap.pageNo-2;
                lastPage = $scope.searchMap.pageNo+2;
            }
        }else {
            $scope.firstDot = false;//前无点
            $scope.lastDot  =false;//后无点
        }
        //循环产生页码标签
        for(var i = firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        if(pageNo<1||pageNo>$scope.resultMap.totalPages){
            return;
        }

        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    }

    //判断是否第一页
    $scope.isStartPage = function () {
        if($scope.searchMap.pageNo==1){
            return true;
        }else {
            return false;
        }
    }

    //判断是否最终页
    $scope.isEndPage = function () {
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    }

    //增加排序规则
    $scope.sortsearch = function (field, sort) {
        $scope.searchMap.sortField = field;
        $scope.searchMap.sort = sort;
        $scope.search();
    }

    //判断关键字是否是品牌
    $scope.keywordsIsBrand = function () {
        for(var i =0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    //接收首页传出的关键字
    $scope.localKeywords = function () {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();
    }
});