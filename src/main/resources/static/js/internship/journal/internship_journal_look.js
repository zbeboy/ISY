/**
 * Created by lenovo on 2016-12-18.
 */
require(["jquery", "handlebars", "nav_active", "jquery.address"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            nav: '/web/menu/internship/journal'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         返回
         */
        $('#page_back').click(function () {
            window.history.go(-1);
        });

    });