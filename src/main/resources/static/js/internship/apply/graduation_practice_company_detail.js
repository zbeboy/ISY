/**
 * Created by lenovo on 2017/1/18.
 */
require(["jquery", "nav_active"],
    function ($, nav_active) {
        /*
         ajax url.
         */
        var ajax_url = {
            nav: '/web/menu/internship/apply'
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