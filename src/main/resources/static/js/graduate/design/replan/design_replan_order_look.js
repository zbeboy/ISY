/**
 * Created by zbeboy on 2017/7/19.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address",
    "jquery.showLoading", "tablesaw", "bootstrap"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/anyone/graduate/design/defense/order/data',
        adjust_url: '/web/graduate/design/replan/order/adjust',
        nav: '/web/menu/graduate/design/replan',
        back: '/web/graduate/design/replan/order'
    };

    /*
     参数id
     */
    var paramId = {
        studentName: '#search_student_name',
        studentNumber: '#search_student_number'
    };

    /*
     参数
     */
    var param = {
        graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
        defenseGroupId: init_page_param.defenseGroupId,
        studentName: $(paramId.studentName).val(),
        studentNumber: $(paramId.studentNumber).val()
    };

    /*
    web storage key.
    */
    var webStorageKey = {
        STUDENT_NAME: 'GRADUATE_DESIGN_REPLAN_ORDER_LOOK_STUDENT_NAME_SEARCH_' + init_page_param.defenseGroupId,
        STUDENT_NUMBER: 'GRADUATE_DESIGN_REPLAN_ORDER_LOOK_STUDENT_NUMBER_SEARCH_' + init_page_param.defenseGroupId
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

    /*
     清空参数
     */
    function cleanParam() {
        $(paramId.studentName).val('');
        $(paramId.studentNumber).val('');
    }

    /**
     * 刷新查询参数
     */
    function refreshSearch() {
        if (typeof(Storage) !== "undefined") {
            sessionStorage.setItem(webStorageKey.STUDENT_NAME, $(paramId.studentName).val());
            sessionStorage.setItem(webStorageKey.STUDENT_NUMBER, $(paramId.studentNumber).val());
        }
    }

    /*
     搜索
     */
    $('#search').click(function () {
        refreshSearch();
        init();
    });

    /*
     重置
     */
    $('#reset_search').click(function () {
        cleanParam();
        refreshSearch();
        init();
    });

    $(paramId.studentName).keyup(function (event) {
        if (event.keyCode === 13) {
            refreshSearch();
            init();
        }
    });

    $(paramId.studentNumber).keyup(function (event) {
        if (event.keyCode === 13) {
            refreshSearch();
            init();
        }
    });

    var tableData = '#tableData';

    /*
     返回
     */
    $('#page_back').click(function () {
        $.address.value(ajax_url.back + '?id=' + init_page_param.graduationDesignReleaseId);
    });

    init();
    initSearchInput();

    function init() {
        initSearchContent();
        startLoading();
        $.get(web_path + ajax_url.data_url, param, function (data) {
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

    /*
    初始化搜索内容
    */
    function initSearchContent() {
        var studentName = null;
        var studentNumber = null;
        if (typeof(Storage) !== "undefined") {
            studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
            studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
        }
        if (studentName !== null) {
            param.studentName = studentName;
        } else {
            param.studentName = $(paramId.studentName).val();
        }

        if (studentNumber !== null) {
            param.studentNumber = studentNumber;
        } else {
            param.studentNumber = $(paramId.studentNumber).val();
        }

    }

    /*
    初始化搜索框
    */
    function initSearchInput() {
        var studentName = null;
        var studentNumber = null;
        if (typeof(Storage) !== "undefined") {
            studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
            studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
        }
        if (studentName !== null) {
            $(paramId.studentName).val(studentName);
        }

        if (studentNumber !== null) {
            $(paramId.studentNumber).val(studentNumber);
        }
    }

    /**
     * 列表数据
     * @param data 数据
     */
    function listData(data) {
        var template = Handlebars.compile($("#order-template").html());
        Handlebars.registerHelper('subject', function () {
            var v = '';
            var html = '';
            if (this.subject !== null) {
                if (this.subject.length > 5) {
                    v = this.subject.substring(0, 5) + '...';
                } else {
                    v = this.subject;
                }
                html = '<button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="top" title="' + this.subject + '">' + v + '</button>';
            }
            return html;
        });
        $(tableData).html(template(data));
        $('[data-toggle="tooltip"]').tooltip();
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
    }

    /*
     调换
     */
    $(tableData).delegate('.adjust', "click", function () {
        var id = $(this).attr('data-id');
        $('#adjustDefenseOrderId').val(id);
        $('#adjustModal').modal('show');
    });

    /**
     * 保存调整
     */
    $('#saveAdjust').click(function () {
        $.post(web_path + ajax_url.adjust_url, $('#adjustForm').serialize(), function (data) {
            if (data.state) {
                Messenger().post({
                    message: data.msg,
                    type: 'success',
                    showCloseButton: true
                });
                $('#adjustModal').modal('hide');
                init();
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