app.controller('contentController',function ($scope, contentService) {

    //设置集合存放广告图片
    $scope.contentList = [];

    //轮播图
    $scope.findContent = function (categoryId) {
        contentService.findContent(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        )
    }

    //跳转搜索页
    $scope.search = function () {
        location.href = "http://127.0.0.1:9003/search.html#?keywords="+$scope.keywords;
    }

});