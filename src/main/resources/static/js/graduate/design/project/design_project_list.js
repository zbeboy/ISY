/**
 * Created by zbeboy on 2017/5/26.
 */
//# sourceURL=design_project_list.js
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw", "check.all"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/project/list/data',
        condition: '/web/graduate/design/project/condition',
        add: '/web/graduate/design/project/list/add',
        edit: '/web/graduate/design/project/list/edit',
        del: '/web/graduate/design/project/list/delete',
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
        var template = Handlebars.compile($("#project-template").html());
        $(tableData).html(template(data));
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
        // 调用全选插件
        $.fn.check({checkall_name: "checkall", checkbox_name: "check"});
    }

    /*
     添加
     */
    $('#project_add').click(function () {
        $.post(ajax_url.condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            if (data.state) {
                $.address.value(ajax_url.add + '?id=' + init_page_param.graduationDesignReleaseId);
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
     批量删除
     */
    $('#project_dels').click(function () {
        var graduationDesignPlanIds = [];
        var ids = $('input[name="check"]:checked');
        for (var i = 0; i < ids.length; i++) {
            graduationDesignPlanIds.push($(ids[i]).val());
        }

        if (graduationDesignPlanIds.length > 0) {
            var msg;
            msg = Messenger().post({
                message: "确定删除选中的吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            $.post(ajax_url.condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                                if (data.state) {
                                    dels(graduationDesignPlanIds);
                                } else {
                                    Messenger().post({
                                        message: data.msg,
                                        type: 'error',
                                        showCloseButton: true
                                    });
                                }
                            });
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
        } else {
            Messenger().post("未发现有选中的!");
        }


    });

    /*
     编辑
     */
    $(tableData).delegate('.edit', "click", function () {
        var graduationDesignPlanId = $(this).attr('data-id');
        $.post(ajax_url.condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            if (data.state) {
                $.address.value(ajax_url.edit + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPlanId=' + graduationDesignPlanId);
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
     删除
     */
    $(tableData).delegate('.del', "click", function () {
        var graduationDesignPlanId = $(this).attr('data-id');
        $.post(ajax_url.condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            if (data.state) {
                planDel(graduationDesignPlanId);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    function planDel(graduationDesignPlanId) {
        var msg;
        msg = Messenger().post({
            message: "确定删除吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        del(graduationDesignPlanId);
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
    }

    function del(graduationDesignPlanId) {
        sendDelAjax(graduationDesignPlanId, '删除');
    }

    function dels(graduationDesignPlanIds) {
        sendDelAjax(graduationDesignPlanIds.join(","), '批量删除');
    }

    /**
     * 删除ajax
     * @param graduationDesignPlanId
     * @param message
     */
    function sendDelAjax(graduationDesignPlanId, message) {
        Messenger().run({
            successMessage: message + '成功',
            errorMessage: message + '失败',
            progressMessage: '正在' + message + '....'
        }, {
            url: web_path + ajax_url.del,
            type: 'post',
            data: {graduationDesignPlanIds: graduationDesignPlanId, id: init_page_param.graduationDesignReleaseId},
            success: function (data) {
                if (data.state) {
                    init();
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