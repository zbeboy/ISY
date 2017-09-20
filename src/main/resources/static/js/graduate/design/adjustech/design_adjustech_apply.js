/**
 * Created by zbeboy on 2017/5/19.
 */
//# sourceURL=design_pharmtech_apply.js
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/adjustech/apply/data',
        back: '/web/menu/graduate/design/adjustech'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.back);

    function startLoading() {
        // 显示遮罩
        $('#page-wrapper').showLoading();
    }

    function endLoading() {
        // 去除遮罩
        $('#page-wrapper').hideLoading();
    }

    $('#refresh').click(function () {
        init();
    });

    var tableData = '#tableData';

    /*
     返回
     */
    $('#page_back').click(function () {
        $.address.value(ajax_url.back);
    });

    init();

    function init() {
        startLoading();
        $.get(web_path + ajax_url.data_url, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            endLoading();
            if (data.state) {
                listData(data);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    }

    /**
     * 列表数据
     * @param data 数据
     */
    function listData(data) {
        var template = Handlebars.compile($("#teacher-template").html());
        $(tableData).html(template(data));
        $('#teacherTable').tablesaw().data("tablesaw").refresh();
    }
});