/**
 * Created by lenovo on 2016-10-06.
 */
require(["jquery", "handlebars", "datatables.responsive", "dataTables.fixedHeader", "check.all", "messenger"], function ($, Handlebars) {

    /**
     * 用户类型数据展现
     * @param data json数据
     */
    function usersTypeData(data) {
        var template = Handlebars.compile($("#users-type-template").html());

        Handlebars.registerHelper('value', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.usersTypeId));
        });

        Handlebars.registerHelper('name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.usersTypeName));
        });

        return template(data);
    }

    /**
     * 角色数据展现
     * @param data json数据
     */
    function roleData(data) {
        var template = Handlebars.compile($("#role-template").html());

        Handlebars.registerHelper('value', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.roleEnName));
        });

        Handlebars.registerHelper('name', function () {
            return new Handlebars.SafeString(Handlebars.escapeExpression(this.roleName));
        });

        return template(data);
    }

    /**
     * 初始化用户类型数据
     */
    $.get(web_path + getAjaxUrl().usersTypeData, function (data) {
        var html = usersTypeData(data);
        $(getPassParamId().usersType).html(html);
        $(getWaitParamId().usersType).html(html);
    });

    /*
     ajax url
     */
    function getAjaxUrl() {
        return {
            passData: '/web/platform/users/pass/data',
            waitData: '/web/platform/users/wait/data',
            usersTypeData: '/web/platform/users/type/data',
            roleData: '/special/channel/users/role/data',
            saveRole: '/special/channel/users/role/save',
            updateEnabled: '/special/channel/users/update/enabled',
            deleteUsers: '/special/channel/users/deletes'
        };
    }

    /**
     * pass tab param
     * @returns {{username: string, mobile: string, usersType: string}}
     */
    function getPassParamId() {
        return {
            username: '#pass_search_username',
            mobile: '#pass_search_mobile',
            usersType: '#pass_users_type'
        };
    }

    /*
     参数
     */
    var passParam = {
        username: '',
        mobile: '',
        usersType: ''
    };

    /*
     得到参数
     */
    function getPassParam() {
        return passParam;
    }

    /**
     * init pass tab param.
     * @returns {{username: (*|jQuery), mobile: (*|jQuery), usersType: (*|jQuery)}}
     */
    function initPassParam() {
        passParam.username = $(getPassParamId().username).val();
        passParam.mobile = $(getPassParamId().mobile).val();
        passParam.usersType = $(getPassParamId().usersType).val();
    }

    /**
     * clean pass tab param.
     */
    function cleanPassParam() {
        $(getPassParamId().username).val('');
        $(getPassParamId().mobile).val('');
        var childrens = $(getPassParamId().usersType).children();
        for (var i = 0; i < childrens.length; i++) {
            if (Number($(childrens[i]).val()) == 0) {
                $(childrens[i]).prop('selected', true);
                break;
            }
        }
    }

    var passId = $('#pass');
    var waitId = $('#wait');

    // pass tab table.
    var passTable = null;

    $(getPassParamId().username).keyup(function (event) {
        if (event.keyCode == 13) {
            initPassParam();
            passTable.ajax.reload();
        }
    });

    $(getPassParamId().mobile).keyup(function (event) {
        if (event.keyCode == 13) {
            initPassParam();
            passTable.ajax.reload();
        }
    });

    $(getPassParamId().usersType).change(function () {
        initPassParam();
        passTable.ajax.reload();
    });

    passId.delegate('#pass_search', "click", function () {
        initPassParam();
        passTable.ajax.reload();
    });

    passId.delegate('#pass_reset_search', "click", function () {
        cleanPassParam();
        initPassParam();
        passTable.ajax.reload();
    });

    passId.delegate('#pass_refresh', "click", function () {
        passTable.ajax.reload();
    });

    /**
     * init pass tab table.
     */
    function pass() {
        // 预编译模板
        var template = Handlebars.compile($("#operator_button").html());

        // datatables 初始化
        var responsiveHelper = undefined;
        var breakpointDefinition = {
            tablet: 1024,
            phone: 480
        };
        var tableElement = $('#pass_table');

        passTable = tableElement.DataTable({
            fixedHeader: true,
            autoWidth: false,
            preDrawCallback: function () {
                // Initialize the responsive datatables helper once.
                if (!responsiveHelper) {
                    responsiveHelper = new ResponsiveDatatablesHelper(tableElement, breakpointDefinition);
                }
            },
            rowCallback: function (nRow) {
                responsiveHelper.createExpandIcon(nRow);
            },
            drawCallback: function (oSettings) {
                responsiveHelper.respond();
                $('#pass_checkall').prop('checked', false);
                // 调用全选插件
                $.fn.check({checkall_name: "pass_checkall", checkbox_name: "pass_check"});
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[8, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().passData,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getPassParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": null},
                {"data": "realName"},
                {"data": "username"},
                {"data": "mobile"},
                {"data": "roleName"},
                {"data": "usersTypeName"},
                {"data": "enabled"},
                {"data": "langKey"},
                {"data": "joinDate"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.username + '" name="pass_check"/>';
                    }
                },
                {
                    targets: 4,
                    orderable: false
                },
                {
                    targets: 6,
                    render: function (a, b, c, d) {
                        if (c.enabled == 0 || c.enabled == null) {
                            return "<span class='text-danger'>已注销</span>";
                        }
                        return "<span class='text-info'>正常</span>";
                    }
                },
                {
                    targets: 9,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context = null;

                        if (c.enabled == 1) {
                            context =
                            {
                                func: [
                                    {
                                        "name": "注销",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.username,
                                        "role": c.roleName
                                    },
                                    {
                                        "name": "设置角色",
                                        "css": "role",
                                        "type": "info",
                                        "id": c.username,
                                        "role": c.roleName
                                    }
                                ]
                            };
                        } else {
                            context =
                            {
                                func: [
                                    {
                                        "name": "恢复",
                                        "css": "recovery",
                                        "type": "warning",
                                        "id": c.username,
                                        "role": c.roleName
                                    },
                                    {
                                        "name": "设置角色",
                                        "css": "role",
                                        "type": "info",
                                        "id": c.username,
                                        "role": c.roleName
                                    }
                                ]
                            };
                        }

                        return template(context);
                    }
                }
            ],
            "language": {
                "sProcessing": "处理中...",
                "sLengthMenu": "显示 _MENU_ 项结果",
                "sZeroRecords": "没有匹配结果",
                "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
                "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
                "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                "sInfoPostFix": "",
                "sSearch": "搜索:",
                "sUrl": "",
                "sEmptyTable": "表中数据为空",
                "sLoadingRecords": "载入中...",
                "sInfoThousands": ",",
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "上页",
                    "sNext": "下页",
                    "sLast": "末页"
                },
                "oAria": {
                    "sSortAscending": ": 以升序排列此列",
                    "sSortDescending": ": 以降序排列此列"
                }
            },
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-7'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.del', "click", function () {
                    cancel($(this).attr('data-id'));
                });

                tableElement.delegate('.recovery', "click", function () {
                    recovery($(this).attr('data-id'));
                });

                passId.delegate('#pass_dels', "click", function () {
                    cancels();
                });

                passId.delegate('#pass_recoveries', "click", function () {
                    recoveries();
                });

                tableElement.delegate('.role', "click", function () {
                    role($(this).attr('data-id'), $(this).attr('data-role'));
                });
            }
        });

        var global_button = '<button type="button" id="pass_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量注销</button>' +
            '  <button type="button" id="pass_recoveries" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-reply-all"></i>批量恢复</button>' +
            '  <button type="button" id="pass_refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);
    }

    // init
    pass();

    /**
     * 处理角色
     * @param username
     * @param role
     */
    function role(username, role) {
        $.post(web_path + getAjaxUrl().roleData, {username: username}, function (data) {
            var html = roleData(data);
            $('#roles').html(html);
            var roleNames = role.split(' ');
            var roles = $('.role_set');
            for (var i = 0; i < roles.length; i++) {
                for (var j = 0; j < roleNames.length; j++) {
                    if ($(roles[i]).text() === roleNames[j]) {
                        $(roles[i]).prev().prop('checked', true);
                    }
                }
            }
            $('#roleUsername').val(username);
            $('#roleModal').modal('toggle');
        });
    }

    /*
     关闭角色设置modal
     */
    $('#roleModalMiss').click(function () {
        $('#role_error_msg').addClass('hidden').removeClass('text-danger').text('');
        $('#roleModal').modal('hide');
    });

    // 保存角色
    $("#saveRole").click(function () {
        var roles = $('input[name="role"]:checked');
        if (roles.length <= 0) {
            $('#role_error_msg').removeClass('hidden').addClass('text-danger').text('请至少选择一个角色');
        } else {
            $('#role_error_msg').addClass('hidden').removeClass('text-danger').text('');
            var r = [];
            for (var i = 0; i < roles.length; i++) {
                r.push($(roles[i]).val());
            }
            $.post(web_path + getAjaxUrl().saveRole, {
                username: $('#roleUsername').val(),
                roles: r.join(",")
            }, function (data) {
                if (data.state) {
                    $('#roleModal').modal('toggle');
                    if (passTable != null) {
                        passTable.ajax.reload();
                    }
                    if (waitTable != null) {
                        waitTable.ajax.reload();
                    }
                } else {
                    $('#role_error_msg').removeClass('hidden').addClass('text-danger').text(data.msg);
                }
            });
        }
    });

    /**
     * 注销
     * @param username
     */
    function cancel(username) {
        var msg;
        msg = Messenger().post({
            message: "确定注销 '" + username + "' 吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        toCancel(username);
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

    /**
     * 恢复
     * @param username
     */
    function recovery(username) {
        var msg;
        msg = Messenger().post({
            message: "确定恢复 '" + username + "' 吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        toRecovery(username);
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

    /*
     批量注销
     */
    function cancels() {
        var userIds = [];
        var ids = $('input[name="pass_check"]:checked');
        for (var i = 0; i < ids.length; i++) {
            userIds.push($(ids[i]).val());
        }

        if (userIds.length > 0) {
            var msg;
            msg = Messenger().post({
                message: "确定注销选中的用户吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            toCancels(userIds);
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
            Messenger().post("未发现有选中的用户!");
        }
    }

    /*
     批量恢复
     */
    function recoveries() {
        var userIds = [];
        var ids = $('input[name="pass_check"]:checked');
        for (var i = 0; i < ids.length; i++) {
            userIds.push($(ids[i]).val());
        }

        if (userIds.length > 0) {
            var msg;
            msg = Messenger().post({
                message: "确定恢复选中的用户吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            toRecoveries(userIds);
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
            Messenger().post("未发现有选中的用户!");
        }
    }

    function toCancel(username) {
        sendUpdateEnabledAjax(username, '注销', 0);
    }

    function toRecovery(username) {
        sendUpdateEnabledAjax(username, '恢复', 1);
    }

    function toCancels(ids) {
        sendUpdateEnabledAjax(ids.join(","), '批量注销', 0);
    }

    function toRecoveries(ids) {
        sendUpdateEnabledAjax(ids.join(","), '批量恢复', 1);
    }

    /**
     * 发送更新用户状态 ajax.
     * @param username
     * @param message
     * @param enabled
     */
    function sendUpdateEnabledAjax(username, message, enabled) {
        Messenger().run({
            successMessage: message + '用户成功',
            errorMessage: message + '用户失败',
            progressMessage: '正在' + message + '用户....'
        }, {
            url: web_path + getAjaxUrl().updateEnabled,
            type: 'post',
            data: {userIds: username, enabled: enabled},
            success: function (data) {
                if (data.state) {
                    passTable.ajax.reload();
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

    // tab
    $('#myTab').find('a').click(function (e) {
        e.preventDefault();
        var t = $(this).text();
        if (t === '已审核') {
            if (passTable == null) {
                pass();
            } else {
                passTable.ajax.reload();
            }
        } else if (t === '未审核') {
            if (waitTable == null) {
                wait();
            } else {
                waitTable.ajax.reload();
            }
        }
    });

    function getWaitParamId() {
        return {
            username: '#wait_search_username',
            mobile: '#wait_search_mobile',
            usersType: '#wait_users_type'
        };
    }

    /*
     参数
     */
    var waitParam = {
        username: '',
        mobile: '',
        usersType: ''
    };

    /*
     得到参数
     */
    function getWaitParam() {
        return waitParam;
    }

    function initWaitParam() {
        waitParam.username = $(getWaitParamId().username).val();
        waitParam.mobile = $(getWaitParamId().mobile).val();
        waitParam.usersType = $(getWaitParamId().usersType).val();
    }

    function cleanWaitParam() {
        $(getWaitParamId().username).val('');
        $(getWaitParamId().mobile).val('');
        var childrens = $(getWaitParamId().usersType).children();
        for (var i = 0; i < childrens.length; i++) {
            if (Number($(childrens[i]).val()) == 0) {
                $(childrens[i]).prop('selected', true);
                break;
            }
        }
    }

    var waitTable = null;

    $(getWaitParamId().username).keyup(function (event) {
        if (event.keyCode == 13) {
            initWaitParam();
            waitTable.ajax.reload();
        }
    });

    $(getWaitParamId().mobile).keyup(function (event) {
        if (event.keyCode == 13) {
            initWaitParam();
            waitTable.ajax.reload();
        }
    });

    $(getWaitParamId().usersType).change(function (event) {
        initWaitParam();
        waitTable.ajax.reload();
    });

    waitId.delegate('#wait_search', "click", function () {
        initWaitParam();
        waitTable.ajax.reload();
    });

    waitId.delegate('#wait_reset_search', "click", function () {
        cleanWaitParam();
        initWaitParam();
        waitTable.ajax.reload();
    });

    waitId.delegate('#wait_refresh', "click", function () {
        waitTable.ajax.reload();
    });

    function wait() {
        // 预编译模板
        var template = Handlebars.compile($("#operator_button").html());

        // datatables 初始化
        var responsiveHelper = undefined;
        var breakpointDefinition = {
            tablet: 1024,
            phone: 480
        };
        var tableElement = $('#wait_table');

        waitTable = tableElement.DataTable({
            fixedHeader: true,
            autoWidth: false,
            preDrawCallback: function () {
                // Initialize the responsive datatables helper once.
                if (!responsiveHelper) {
                    responsiveHelper = new ResponsiveDatatablesHelper(tableElement, breakpointDefinition);
                }
            },
            rowCallback: function (nRow) {
                responsiveHelper.createExpandIcon(nRow);
            },
            drawCallback: function (oSettings) {
                responsiveHelper.respond();
                $('#wait_checkall').prop('checked', false);
                // 调用全选插件
                $.fn.check({checkall_name: "wait_checkall", checkbox_name: "wait_check"});
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[6, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().waitData,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getWaitParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": null},
                {"data": "realName"},
                {"data": "username"},
                {"data": "mobile"},
                {"data": "usersTypeName"},
                {"data": "langKey"},
                {"data": "joinDate"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.username + '" name="wait_check"/>';
                    }
                },
                {
                    targets: 7,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context =
                        {
                            func: [
                                {
                                    "name": "删除",
                                    "css": "delete",
                                    "type": "danger",
                                    "id": c.username,
                                    "role": ''
                                },
                                {
                                    "name": "设置角色",
                                    "css": "role",
                                    "type": "info",
                                    "id": c.username,
                                    "role": ''
                                }
                            ]
                        };

                        return template(context);
                    }
                }
            ],
            "language": {
                "sProcessing": "处理中...",
                "sLengthMenu": "显示 _MENU_ 项结果",
                "sZeroRecords": "没有匹配结果",
                "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
                "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
                "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                "sInfoPostFix": "",
                "sSearch": "搜索:",
                "sUrl": "",
                "sEmptyTable": "表中数据为空",
                "sLoadingRecords": "载入中...",
                "sInfoThousands": ",",
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "上页",
                    "sNext": "下页",
                    "sLast": "末页"
                },
                "oAria": {
                    "sSortAscending": ": 以升序排列此列",
                    "sSortDescending": ": 以降序排列此列"
                }
            },
            "dom": "<'row'<'col-sm-2'l><'#wait_global_button.col-sm-7'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {

                tableElement.delegate('.delete', "click", function () {
                    usersDelete($(this).attr('data-id'));
                });

                tableElement.delegate('.role', "click", function () {
                    role($(this).attr('data-id'), $(this).attr('data-role'));
                });

                waitId.delegate('#wait_deletes', "click", function () {
                    usersDeletes();
                });
            }
        });

        var wait_global_button = '<button type="button" id="wait_deletes" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量删除</button>' +
            '  <button type="button" id="wait_refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#wait_global_button').append(wait_global_button);
    }

    function usersDelete(username) {
        var msg;
        msg = Messenger().post({
            message: "确定删除 '" + username + "' 吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        toDelete(username);
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

    /*
     批量删除
     */
    function usersDeletes() {
        var userIds = [];
        var ids = $('input[name="wait_check"]:checked');
        for (var i = 0; i < ids.length; i++) {
            userIds.push($(ids[i]).val());
        }

        if (userIds.length > 0) {
            var msg;
            msg = Messenger().post({
                message: "确定删除选中的用户吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            toDeletes(userIds);
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
            Messenger().post("未发现有选中的用户!");
        }
    }

    function toDelete(username) {
        sendDeleteAjax(username, '删除');
    }

    function toDeletes(ids) {
        sendDeleteAjax(ids.join(","), '批量删除');
    }

    function sendDeleteAjax(username, message) {
        Messenger().run({
            successMessage: message + '用户成功',
            errorMessage: message + '用户失败',
            progressMessage: '正在' + message + '用户....'
        }, {
            url: web_path + getAjaxUrl().deleteUsers,
            type: 'post',
            data: {username: username},
            success: function (data) {
                if (data.state) {
                    waitTable.ajax.reload();
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