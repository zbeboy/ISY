/**
 * Created by zbeboy on 2017/7/17.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/replan/group/data',
        make_url: '/web/graduate/design/replan/order/make',
        back: '/web/menu/graduate/design/replan'
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
        $.get(web_path + ajax_url.data_url, {
            id: init_page_param.graduationDesignReleaseId,
            defenseArrangementId: init_page_param.defenseArrangementId
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
        var template = Handlebars.compile($("#group-template").html());
        $(tableData).html(template(data));
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
    }

    /*
     设置
     */
    $(tableData).delegate('.make', "click", function () {
        var id = $(this).attr('data-id');
        var msg;
        msg = Messenger().post({
            message: "生成将会覆盖调整，确定吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendMakeAjax(id);
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
     * 发送生成ajax
     * @param id 组id
     */
    function sendMakeAjax(id) {
        Messenger().run({
            successMessage: '生成成功',
            errorMessage: '生成失败',
            progressMessage: '正在生成....'
        }, {
            url: web_path + ajax_url.make_url,
            type: 'post',
            data: {
                defenseGroupId: id,
                id: init_page_param.graduationDesignReleaseId,
                defenseArrangementId: init_page_param.defenseArrangementId
            },
            success: function (data) {
                if (!data.state) {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            },
            error: function (xhr) {
                if ((xhr != null ? xhr.status : void 0) === 404) {
                    return "请求失败";
                }
                return true;
            }
        });
    }
});