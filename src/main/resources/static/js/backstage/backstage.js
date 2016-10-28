/**
 * Created by lenovo on 2016-08-19.
 */
requirejs.config({
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "jquery.showLoading": web_path + "/plugin/loading/js/jquery.showLoading.min",
        "check.all": web_path + "/plugin/checkall/checkall",
        "datatables.responsive": web_path + "/plugin/datatables/js/datatables.responsive",
        "datatables.net": web_path + "/plugin/datatables/js/jquery.dataTables.min",
        "datatables.bootstrap": web_path + "/plugin/datatables/js/dataTables.bootstrap.min",
        "csrf": web_path + "/js/util/csrf",
        "com": web_path + "/js/util/com",
        "nav": web_path + "/js/util/nav",
        "constants": web_path + "/js/util/constants",
        "ajax_loading_view": web_path + "/js/util/ajax_loading_view"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "check.all": {
            deps: ["jquery"]
        },
        "datatables.responsive": {
            deps: ["datatables.bootstrap"]
        },
        "messenger": {
            deps: ["jquery"]
        },
        "bootstrap-treeview": {
            deps: ["jquery"]
        },
        "jquery.showLoading": {
            deps: ["jquery"]
        }
    }
});

/*
 捕获全局错误
 */
requirejs.onError = function (err) {
    console.log(err.requireType);
    if (err.requireType === 'timeout') {
        console.log('modules: ' + err.requireModules);
    }
    throw err;
};

require(["jquery", "nav", "ajax_loading_view", "csrf", "com",], function ($, nav, loadingView, csrf, com) {

    /*
     init message.
     */
    Messenger.options = {
        extraClasses: 'messenger-fixed messenger-on-bottom messenger-on-right',
        theme: 'flat'
    };


    /**
     * nav active where page loading.
     */
    function addActive(obj){
        var id = $('ul.nav a');
        var li = $('ul.nav li');

        for(var i = 0;i<id.length;i++){
            $(id[i]).removeClass('active').parent().parent().removeClass('in').parent();
        }

        for(var i = 0;i<li.length;i++){
            $(li[i]).removeClass('active');
        }

        var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').collapse('hide');
        }

        var parent = $(obj).addClass('active').parent().parent().addClass('in').parent();
        if (parent.is('li')) {
            parent.addClass('active');
        }

    }

    /*
    动态链接点击效果
     */
    $(document).delegate('.dy_href',"click",function(){
        addActive(this);
    });
});