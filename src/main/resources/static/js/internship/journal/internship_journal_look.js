/**
 * Created by lenovo on 2016-12-18.
 */
require(["jquery", "handlebars", "nav_active",, "jquery.address"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            nav:'/web/menu/internship/journal',
            back:'/web/internship/journal/my/list'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back + '?id=' + $('#internshipReleaseId').val());
        });

    });