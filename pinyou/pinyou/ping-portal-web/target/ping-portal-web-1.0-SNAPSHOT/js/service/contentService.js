app.service('contentService',function ($http) {

    //轮播图
    this.findContent = function (categoryId) {
        return $http.get('content/findContent.do?categoryId='+categoryId);
    }

})