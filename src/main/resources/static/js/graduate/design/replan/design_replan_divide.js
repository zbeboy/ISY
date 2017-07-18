/**
 * Created by zbeboy on 2017/7/13.
 */
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address",
    "jquery.showLoading", "tablesaw", "bootstrap"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        data_url: '/web/graduate/design/replan/divide/data',
        group_url: '/web/graduate/design/replan/divide/groups',
        group_info: '/web/graduate/design/replan/divide/group',
        leader_url: '/web/graduate/design/replan/divide/leader',
        save: '/web/graduate/design/replan/divide/save',
        back: '/web/menu/graduate/design/replan'
    };

    /*
     参数id
     */
    var paramId = {
        realName: '#search_name',
        defenseGroupId: '#select_group'
    };

    /*
     参数
     */
    var param = {
        graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
        realName: $(paramId.realName).val(),
        defenseGroupId: $(paramId.defenseGroupId).val()
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

    /*
     清空参数
     */
    function cleanParam() {
        $(paramId.realName).val('');
        $(paramId.defenseGroupId).val('');
    }

    /**
     * 刷新查询参数
     */
    function refreshSearch() {
        param.realName = $(paramId.realName).val();
        param.defenseGroupId = $(paramId.defenseGroupId).val();
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

    $(paramId.realName).keyup(function (event) {
        if (event.keyCode === 13) {
            refreshSearch();
            init();
        }
    });

    $(paramId.defenseGroupId).change(function () {
        refreshSearch();
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
    initGroupData();

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
     * 初始化组数据
     */
    function initGroupData() {
        startLoading();
        $.get(web_path + ajax_url.group_url, param, function (data) {
            endLoading();
            if (data.state) {
                groupData(data);
                groupFormData(data);
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
        var template = Handlebars.compile($("#group-member-template").html());
        Handlebars.registerHelper('is_leader', function () {
            var isLeader = '';
            if (this.graduationDesignTeacherId === this.leaderId) {
                isLeader = '是';
            }
            return new Handlebars.SafeString(Handlebars.escapeExpression(isLeader));
        });
        $(tableData).html(template(data));
        $('#tablesawTable').tablesaw().data("tablesaw").refresh();
    }

    /**
     * 组数据
     * @param data
     */
    function groupData(data) {
        var template = Handlebars.compile($("#group-template").html());
        Handlebars.registerHelper('group_name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.defenseGroupName));
        });
        $(paramId.defenseGroupId).html(template(data));
    }

    /***
     * 初始化group form
     * @param data
     */
    function groupFormData(data) {
        var template = Handlebars.compile($("#group-form-template").html());
        Handlebars.registerHelper('group_name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.defenseGroupName));
        });
        var groupData = $('#groupData');
        groupData.html(template(data));
        $(groupData.children()[0]).remove();
    }

    /*
     设置
     */
    $(tableData).delegate('.edit', "click", function () {
        var id = $(this).attr('data-id');
        $.post(web_path + ajax_url.group_info, {graduationDesignTeacherId: id}, function (data) {
            if (data.state) {
                $('#groupGraduationDesignTeacherId').val(id);
                $('#groupNote').val(data.objectResult.note);
                selectedGroup(data.objectResult.defenseGroupId);
                $('#setGroupModal').modal('show');
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
     设置
     */
    $(tableData).delegate('.setLeader', "click", function () {
        var id = $(this).attr('data-id');
        var groupId = $(this).attr('data-group');
        var name = $(this).attr('data-name');
        var msg;
        msg = Messenger().post({
            message: "确定设置 " + name + " 为组长吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendLeaderAjax(id, groupId);
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
     * 发送设置组长ajax
     * @param id 毕业设计指导教师id
     * @param groupId 组id
     */
    function sendLeaderAjax(id, groupId) {
        $.post(web_path + ajax_url.leader_url, {
            graduationDesignTeacherId: id,
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

    /**
     * 打开模态框时选中组
     */
    function selectedGroup(defenseGroupId) {
        var groups = $('.group_id');
        for (var i = 0; i < groups.length; i++) {
            if ($(groups[i]).val() === defenseGroupId) {
                $(groups[i]).prop('checked', true);
                break;
            } else {
                $(groups[i]).prop('checked', false);
            }
        }
    }

    /**
     * 保存组设置
     */
    $('#saveSetGroup').click(function () {
        $.post(web_path + ajax_url.save, $('#groupForm').serialize(), function (data) {
            if (data.state) {
                Messenger().post({
                    message: data.msg,
                    type: 'success',
                    showCloseButton: true
                });
                $('#setGroupModal').modal('hide');
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