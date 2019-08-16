app.service("loginService",function ($http) {
    //showName
    this.showName = function () {
        return $http.get('../login/name.do');
    }
})