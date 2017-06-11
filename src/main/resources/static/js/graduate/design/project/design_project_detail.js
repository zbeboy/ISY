/**
 * Created by lenovo on 2017-06-11.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/project/list/data',
        download: '/web/graduate/design/project/list/download',
        download_condition:'/web/graduate/design/project/list/condition',
        nav: '/web/menu/graduate/design/project',
        back: '/web/graduate/design/project/list'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.nav);

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
        $.address.value(ajax_url.back + '?id=' + init_page_param.graduationDesignReleaseId);
    });

    init();

    function init() {
        startLoading();
        $.get(web_path + ajax_url.data_url, {
            id: init_page_param.graduationDesignReleaseId,
            staffId: init_page_param.staffId
        }, function (data) {
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
        var template = Handlebars.compile($("#project-template").html());
        $(tableData).html(template(data));
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
    }

    /*
     下载
     */
    $('#project_down').click(function () {
        $.post(web_path + ajax_url.download_condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            if (data.state) {
                window.location.href = web_path + ajax_url.download + '?id=' + init_page_param.graduationDesignReleaseId + '&staffId=' + init_page_param.staffId;
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