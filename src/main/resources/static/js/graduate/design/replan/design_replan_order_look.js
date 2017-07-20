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
        secretary_url: '/web/graduate/design/replan/order/secretary',
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
        param.studentName = $(paramId.studentName).val();
        param.studentNumber = $(paramId.studentNumber).val();
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

    function init() {
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

    /**
     * 列表数据
     * @param data 数据
     */
    function listData(data) {
        var template = Handlebars.compile($("#order-template").html());
        Handlebars.registerHelper('is_secretary', function () {
            var isSecretary = '';
            if (this.studentId === this.secretaryId) {
                isSecretary = '是';
            }
            return new Handlebars.SafeString(Handlebars.escapeExpression(isSecretary));
        });
        $(tableData).html(template(data));
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

    /*
     设置组长
     */
    $(tableData).delegate('.setSecretary', "click", function () {
        var id = $(this).attr('data-id');
        var groupId = init_page_param.defenseGroupId;
        var name = $(this).attr('data-name');
        var msg;
        msg = Messenger().post({
            message: "确定设置 " + name + " 为秘书吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendSecretaryAjax(id, groupId);
                    }
                },
                cancel: {
                    label: '取消',
                    action: function () {
                        return msg.cancel();
                    }
                }
            }
        });
    });

    /**
     * 发送设置秘书ajax
     * @param id 学生id
     * @param groupId 组id
     */
    function sendSecretaryAjax(id, groupId) {
        $.post(web_path + ajax_url.secretary_url, {
            studentId: id,
            defenseGroupId: groupId,
            id: init_page_param.graduationDesignReleaseId
        }, function (data) {
            if (data.state) {
                init();
                Messenger().post({
                    message: data.msg,
                    type: 'success',
                    showCloseButton: true
                });
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    }

});