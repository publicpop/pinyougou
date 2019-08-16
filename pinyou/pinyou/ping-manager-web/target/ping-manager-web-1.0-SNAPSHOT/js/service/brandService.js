app.service('brandService',function ($http) {

    //查询所有
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    };

    //id条件查询
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    };

    //批量删除
    this.delete = function (ids) {
        return $http.get('../brand/delete.do?ids='+ids);
    };

    //添加数据
    this.add = function (entity) {
        return $http.post('../brand/add.do',entity);
    };

    //修改数据
    this.update = function (entity) {
        return $http.post('../brand/update.do',entity);
    };


    //条件查询
    this.search = function (page, size,searchentity) {
        return $http.post('../brand/search.do?page=' + page + '&size=' + size,searchentity);
    };

    //查询品牌数据
    this.selectOptionList = function () {
        return $http.get('../brand/selectOptionList.do');
    };


});
