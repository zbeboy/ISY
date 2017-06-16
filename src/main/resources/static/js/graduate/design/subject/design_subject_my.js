/**
 * Created by zbeboy on 2017/6/5.
 */
require(["jquery", "handlebars", "nav_active", "quill", "jquery.address"],
    function ($, Handlebars, nav_active, Quill) {

        /*
         ajax url.
         */
        var ajax_url = {
            edit: '/web/graduate/design/subject/my/edit',
            operator_condition:'/web/graduate/design/subject/my/operator/condition',
            nav: '/web/menu/graduate/design/subject'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            content: '#content',
            htmlData: '#htmlData',
            edit: '#subject_edit'
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

        /*
        编辑
         */
        $(paramId.edit).click(function () {
            var id = init_page_param.graduationDesignReleaseId;
            $.post(ajax_url.operator_condition, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.edit + '?id=' + id);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        });
    });