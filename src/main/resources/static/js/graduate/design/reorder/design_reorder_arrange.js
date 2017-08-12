/**
 * Created by zbeboy on 2017/7/19.
 */
require(["jquery", "handlebars", "nav_active", "jquery.address"],
    function ($, Handlebars, nav_active) {
        /*
         ajax url.
         */
        var ajax_url = {
            back: '/web/menu/graduate/design/reorder'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });
    });