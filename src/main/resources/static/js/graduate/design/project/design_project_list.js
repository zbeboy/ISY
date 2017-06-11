/**
 * Created by zbeboy on 2017/6/9.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/project/list/teachers',
        detail: '/web/graduate/design/project/list/detail',
        list_condition: '/web/graduate/design/project/list/condition',
        students:'/web/graduate/design/project/list/students',
        student_condition:'/web/graduate/design/project/student/condition',
        download:'/web/graduate/design/project/list/download',
        back: '/web/menu/graduate/design/project'
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

    /*
     Ta的规划
     */
    $(tableData).delegate('.project', "click", function () {
        var staffId = $(this).attr('data-id');
        var graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
        $.post(web_path + ajax_url.list_condition, {id: graduationDesignReleaseId}, function (data) {
            if (data.state) {
                $.address.value(ajax_url.detail + '?id=' + graduationDesignReleaseId + '&staffId=' + staffId);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    /*
     Ta的学生
     */
    $(tableData).delegate('.students', "click", function () {
        var staffId = $(this).attr('data-id');
        var graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
        $.post(web_path + ajax_url.student_condition, {id: graduationDesignReleaseId}, function (data) {
            if (data.state) {
                $.address.value(ajax_url.students + '?id=' + graduationDesignReleaseId + '&staffId=' + staffId);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    /*
     下载
     */
    $(tableData).delegate('.download', "click", function () {
        var staffId = $(this).attr('data-id');
        var graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
        $.post(web_path + ajax_url.list_condition, {id: graduationDesignReleaseId}, function (data) {
            if (data.state) {
                window.location.href = web_path + ajax_url.download + '?id=' + init_page_param.graduationDesignReleaseId + '&staffId=' + staffId;
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