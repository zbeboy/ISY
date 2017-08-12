/**
 * Created by zbeboy on 2017/7/31.
 */
//# sourceURL=design_reorder_question.js
require(["jquery", "handlebars", "nav_active", "quill", "jquery.address"],
    function ($, Handlebars, nav_active, Quill) {

        /*
         ajax url.
         */
        var ajax_url = {
            nav: '/web/menu/graduate/design/reorder'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            content: '#content',
            htmlData: '#htmlData'
        };

        /*
         返回
         */
        $('#page_back').click(function () {
            window.history.go(-1);
        });

        // 初始化内容与感想富文本框
        var quill = new Quill(paramId.content, {
            theme: 'bubble'
        });

        init();

        function init() {
            quill.enable(false);
            if ($(paramId.htmlData).val() !== '') {
                quill.setContents(JSON.parse($(paramId.htmlData).val()));
            }
        }
    });