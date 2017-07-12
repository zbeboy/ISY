/**
 * Created by zbeboy on 2017/7/11.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw", "check.all"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/replan/group/data',
        add: '/web/graduate/design/replan/group/add',
        edit: '/web/graduate/design/replan/group/edit',
        del: '/web/graduate/design/replan/group/del',
        condition: '/web/graduate/design/replan/condition',
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
        // 调用全选插件
        $.fn.check({checkall_name: "checkall", checkbox_name: "check"});
    }

    /*
     添加
     */
    $('#group_add').click(function () {
        $.address.value(ajax_url.add + '?id=' + init_page_param.graduationDesignReleaseId);
    });

    /*
     批量删除
     */
    $('#group_dels').click(function () {
        var defenseGroupIds = [];
        var ids = $('input[name="check"]:checked');
        for (var i = 0; i < ids.length; i++) {
            defenseGroupIds.push($(ids[i]).val());
        }

        if (defenseGroupIds.length > 0) {
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
                                    dels(defenseGroupIds);
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
        var id = $(this).attr('data-id');
        $.address.value(ajax_url.edit + '?id=' + init_page_param.graduationDesignReleaseId + '&defenseGroupId=' + id);
    });

    /*
     删除
     */
    $(tableData).delegate('.del', "click", function () {
        var defenseGroupId = $(this).attr('data-id');
        var defenseGroupName = $(this).attr('data-name');
        $.post(ajax_url.condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            if (data.state) {
                groupDel(defenseGroupId, defenseGroupName);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    function groupDel(defenseGroupId, defenseGroupName) {
        var msg;
        msg = Messenger().post({
            message: "确定删除组 " + defenseGroupName + " 吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        del(defenseGroupId);
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

    function del(defenseGroupId) {
        sendDelAjax(defenseGroupId, '删除');
    }

    function dels(defenseGroupId) {
        sendDelAjax(defenseGroupId.join(","), '批量删除');
    }

    /**
     * 删除ajax
     * @param defenseGroupId
     * @param message
     */
    function sendDelAjax(defenseGroupId, message) {
        Messenger().run({
            successMessage: message + '成功',
            errorMessage: message + '失败',
            progressMessage: '正在' + message + '....'
        }, {
            url: web_path + ajax_url.del,
            type: 'post',
            data: {defenseGroupIds: defenseGroupId, id: init_page_param.graduationDesignReleaseId},
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