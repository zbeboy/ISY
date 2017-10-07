/**
 * Created by lenovo on 2016-10-06.
 */
require(["jquery", "handlebars", "datatables.responsive", "check.all", "messenger"], function ($, Handlebars) {

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
            realName: '#pass_search_real_name',
            username: '#pass_search_username',
            mobile: '#pass_search_mobile',
            usersType: '#pass_users_type'
        };
    }

    /*
     参数
     */
    var passParam = {
        realName: '',
        username: '',
        mobile: '',
        usersType: ''
    };

    /*
     web storage key.
    */
    var webStorageKey = {
        PASS_REAL_NAME: 'PLATFORM_USERS_PASS_REAL_NAME_SEARCH',
        PASS_USERNAME: 'PLATFORM_USERS_PASS_USERNAME_SEARCH',
        PASS_MOBILE: 'PLATFORM_USERS_PASS_MOBILE_SEARCH',
        PASS_USERS_TYPE: 'PLATFORM_USERS_PASS_USERS_TYPE_SEARCH',
        WAIT_REAL_NAME: 'PLATFORM_USERS_WAIT_REAL_NAME_SEARCH',
        WAIT_USERNAME: 'PLATFORM_USERS_WAIT_USERNAME_SEARCH',
        WAIT_MOBILE: 'PLATFORM_USERS_WAIT_MOBILE_SEARCH',
        WAIT_USERS_TYPE: 'PLATFORM_USERS_WAIT_USERS_TYPE_SEARCH'
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
        passParam.realName = $(getPassParamId().realName).val();
        passParam.username = $(getPassParamId().username).val();
        passParam.mobile = $(getPassParamId().mobile).val();
        passParam.usersType = $(getPassParamId().usersType).val();
        if (typeof(Storage) !== "undefined") {
            sessionStorage.setItem(webStorageKey.PASS_REAL_NAME, passParam.realName);
            sessionStorage.setItem(webStorageKey.PASS_USERNAME, passParam.username);
            sessionStorage.setItem(webStorageKey.PASS_MOBILE, passParam.mobile);
            sessionStorage.setItem(webStorageKey.PASS_USERS_TYPE, passParam.usersType);
        }
    }

    /*
     初始化搜索内容
    */
    function initPassSearchContent() {
        var realName = null;
        var username = null;
        var mobile = null;
        var usersType = null;
        if (typeof(Storage) !== "undefined") {
            realName = sessionStorage.getItem(webStorageKey.PASS_REAL_NAME);
            username = sessionStorage.getItem(webStorageKey.PASS_USERNAME);
            mobile = sessionStorage.getItem(webStorageKey.PASS_MOBILE);
            usersType = sessionStorage.getItem(webStorageKey.PASS_USERS_TYPE);
        }
        if (realName !== null) {
            passParam.realName = realName;
        }

        if (username !== null) {
            passParam.username = username;
        }

        if (mobile !== null) {
            passParam.mobile = mobile;
        }

        if (usersType !== null) {
            passParam.usersType = usersType;
        }
    }

    /*
    初始化搜索框
    */
    function initPassSearchInput() {
        var realName = null;
        var username = null;
        var mobile = null;
        var usersType = null;
        if (typeof(Storage) !== "undefined") {
            realName = sessionStorage.getItem(webStorageKey.PASS_REAL_NAME);
            username = sessionStorage.getItem(webStorageKey.PASS_USERNAME);
            mobile = sessionStorage.getItem(webStorageKey.PASS_MOBILE);
            usersType = sessionStorage.getItem(webStorageKey.PASS_USERS_TYPE);
        }
        if (realName !== null) {
            $(getPassParamId().realName).val(realName);
        }

        if (username !== null) {
            $(getPassParamId().username).val(username);
        }

        if (mobile !== null) {
            $(getPassParamId().mobile).val(mobile);
        }

        if (usersType !== null) {
            $(getPassParamId().usersType).val(usersType);
        }
    }

    /**
     * clean pass tab param.
     */
    function cleanPassParam() {
        $(getPassParamId().realName).val('');
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

    $(getPassParamId().realName).keyup(function (event) {
        if (event.keyCode == 13) {
            initPassParam();
            passTable.ajax.reload();
        }
    });

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
     * 获取分页信息
     * @returns {number}
     */
    function getPassPage() {
        var page = 0;
        if (passTable) {
            page = passTable.page();
        }
        return page;
    }

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
                    initPassSearchContent();
                    var searchParam = getPassParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.extra_page = getPassPage();
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
                // 初始化搜索框中内容
                initPassSearchInput();
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
            realName: '#wait_search_real_name',
            username: '#wait_search_username',
            mobile: '#wait_search_mobile',
            usersType: '#wait_users_type'
        };
    }

    /*
     参数
     */
    var waitParam = {
        realName: '',
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
        waitParam.realName = $(getWaitParamId().realName).val();
        waitParam.username = $(getWaitParamId().username).val();
        waitParam.mobile = $(getWaitParamId().mobile).val();
        waitParam.usersType = $(getWaitParamId().usersType).val();
        if (typeof(Storage) !== "undefined") {
            sessionStorage.setItem(webStorageKey.WAIT_REAL_NAME, waitParam.realName);
            sessionStorage.setItem(webStorageKey.WAIT_USERNAME, waitParam.username);
            sessionStorage.setItem(webStorageKey.WAIT_MOBILE, waitParam.mobile);
            sessionStorage.setItem(webStorageKey.WAIT_USERS_TYPE, waitParam.usersType);
        }
    }

    /*
     初始化搜索内容
    */
    function initWaitSearchContent() {
        var realName = null;
        var username = null;
        var mobile = null;
        var usersType = null;
        if (typeof(Storage) !== "undefined") {
            realName = sessionStorage.getItem(webStorageKey.WAIT_REAL_NAME);
            username = sessionStorage.getItem(webStorageKey.WAIT_USERNAME);
            mobile = sessionStorage.getItem(webStorageKey.WAIT_MOBILE);
            usersType = sessionStorage.getItem(webStorageKey.WAIT_USERS_TYPE);
        }
        if (realName !== null) {
            waitParam.realName = realName;
        }

        if (username !== null) {
            waitParam.username = username;
        }

        if (mobile !== null) {
            waitParam.mobile = mobile;
        }

        if (usersType !== null) {
            waitParam.usersType = usersType;
        }
    }

    /*
    初始化搜索框
    */
    function initWaitSearchInput() {
        var realName = null;
        var username = null;
        var mobile = null;
        var usersType = null;
        if (typeof(Storage) !== "undefined") {
            realName = sessionStorage.getItem(webStorageKey.WAIT_REAL_NAME);
            username = sessionStorage.getItem(webStorageKey.WAIT_USERNAME);
            mobile = sessionStorage.getItem(webStorageKey.WAIT_MOBILE);
            usersType = sessionStorage.getItem(webStorageKey.WAIT_USERS_TYPE);
        }
        if (realName !== null) {
            $(getWaitParamId().realName).val(realName);
        }

        if (username !== null) {
            $(getWaitParamId().username).val(username);
        }

        if (mobile !== null) {
            $(getWaitParamId().mobile).val(mobile);
        }

        if (usersType !== null) {
            $(getWaitParamId().usersType).val(usersType);
        }
    }

    function cleanWaitParam() {
        $(getWaitParamId().realName).val('');
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

    $(getWaitParamId().realName).keyup(function (event) {
        if (event.keyCode == 13) {
            initWaitParam();
            waitTable.ajax.reload();
        }
    });

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

    /**
     * 获取分页信息
     * @returns {number}
     */
    function getWaitPage() {
        var page = 0;
        if (waitTable) {
            page = waitTable.page();
        }
        return page;
    }

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
                    initWaitSearchContent();
                    var searchParam = getWaitParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.extra_page = getWaitPage();
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
                        var context = null;
                        if (c.verifyMailbox !== null && c.verifyMailbox === 1) {
                            context =
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
                        } else {
                            context =
                                {
                                    func: [
                                        {
                                            "name": "删除",
                                            "css": "delete",
                                            "type": "danger",
                                            "id": c.username,
                                            "role": ''
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
                // 初始化搜索框中内容
                initWaitSearchInput();
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