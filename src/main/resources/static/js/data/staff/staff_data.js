/**
 * Created by lenovo on 2016-10-16.
 */
require(["jquery", "handlebars", "datatables.responsive", "check.all", "messenger"],
    function ($, Handlebars) {
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

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                passData: '/web/data/staff/pass/data',
                waitData: '/web/data/staff/wait/data',
                roleData: '/special/channel/users/role/data',
                saveRole: '/special/channel/users/role/save',
                updateEnabled: '/special/channel/users/update/enabled',
                deleteUsers: '/special/channel/users/deletes'
            };
        }

        var passId = $('#pass');
        var waitId = $('#wait');

        /**
         * pass tab param
         * @returns {{username: string, mobile: string, usersType: string}}
         */
        function getPassParamId() {
            return {
                school: '#pass_search_school',
                college: '#pass_search_college',
                department: '#pass_search_department',
                post: '#pass_search_post',
                staffNumber: '#pass_search_staff_number',
                username: '#pass_search_username',
                mobile: '#pass_mobile',
                idCard: '#pass_id_card',
                realName: '#pass_real_name',
                sex: '#pass_sex'
            };
        }

        /*
         参数
         */
        var passParam = {
            school: '',
            college: '',
            department: '',
            post: '',
            staffNumber: '',
            username: '',
            mobile: '',
            idCard: '',
            realName: '',
            sex: ''
        };

        /*
         web storage key.
        */
        var webStorageKey = {
            PASS_SCHOOL: 'DATA_STAFF_PASS_SCHOOL_SEARCH',
            PASS_COLLEGE: 'DATA_STAFF_PASS_COLLEGE_SEARCH',
            PASS_DEPARTMENT: 'DATA_STAFF_PASS_DEPARTMENT_SEARCH',
            PASS_POST: 'DATA_STAFF_PASS_POST_SEARCH',
            PASS_STAFF_NUMBER: 'DATA_STAFF_PASS_STAFF_NUMBER_SEARCH',
            PASS_USERNAME: 'DATA_STAFF_PASS_USERNAME_SEARCH',
            PASS_MOBILE: 'DATA_STAFF_PASS_MOBILE_SEARCH',
            PASS_ID_CARD: 'DATA_STAFF_PASS_ID_CARD_SEARCH',
            PASS_REAL_NAME: 'DATA_STAFF_PASS_REAL_NAME_SEARCH',
            PASS_SEX: 'DATA_STAFF_PASS_SEX_SEARCH',
            WAIT_SCHOOL: 'DATA_STAFF_WAIT_SCHOOL_SEARCH',
            WAIT_COLLEGE: 'DATA_STAFF_WAIT_COLLEGE_SEARCH',
            WAIT_DEPARTMENT: 'DATA_STAFF_WAIT_DEPARTMENT_SEARCH',
            WAIT_MOBILE: 'DATA_STAFF_WAIT_MOBILE_SEARCH',
            WAIT_STAFF_NUMBER: 'DATA_STAFF_WAIT_STAFF_NUMBER_SEARCH',
            WAIT_USERNAME: 'DATA_STAFF_WAIT_USERNAME_SEARCH'
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
            passParam.school = $(getPassParamId().school).val();
            passParam.college = $(getPassParamId().college).val();
            passParam.department = $(getPassParamId().department).val();
            passParam.post = $(getPassParamId().post).val();
            passParam.staffNumber = $(getPassParamId().staffNumber).val();
            passParam.username = $(getPassParamId().username).val();
            passParam.mobile = $(getPassParamId().mobile).val();
            passParam.idCard = $(getPassParamId().idCard).val();
            passParam.realName = $(getPassParamId().realName).val();
            passParam.sex = $(getPassParamId().sex).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.PASS_SCHOOL, passParam.school);
                sessionStorage.setItem(webStorageKey.PASS_COLLEGE, passParam.college);
                sessionStorage.setItem(webStorageKey.PASS_DEPARTMENT, passParam.department);
                sessionStorage.setItem(webStorageKey.PASS_POST, passParam.post);
                sessionStorage.setItem(webStorageKey.PASS_STAFF_NUMBER, passParam.staffNumber);
                sessionStorage.setItem(webStorageKey.PASS_USERNAME, passParam.username);
                sessionStorage.setItem(webStorageKey.PASS_MOBILE, passParam.mobile);
                sessionStorage.setItem(webStorageKey.PASS_ID_CARD, passParam.idCard);
                sessionStorage.setItem(webStorageKey.PASS_REAL_NAME, passParam.realName);
                sessionStorage.setItem(webStorageKey.PASS_SEX, passParam.sex);
            }
        }

        /*
        初始化搜索内容
        */
        function initPassSearchContent() {
            var school = null;
            var college = null;
            var department = null;
            var post = null;
            var staffNumber = null;
            var username = null;
            var mobile = null;
            var idCard = null;
            var realName = null;
            var sex = null;
            if (typeof(Storage) !== "undefined") {
                school = sessionStorage.getItem(webStorageKey.PASS_SCHOOL);
                college = sessionStorage.getItem(webStorageKey.PASS_COLLEGE);
                department = sessionStorage.getItem(webStorageKey.PASS_DEPARTMENT);
                post = sessionStorage.getItem(webStorageKey.PASS_POST);
                staffNumber = sessionStorage.getItem(webStorageKey.PASS_STAFF_NUMBER);
                username = sessionStorage.getItem(webStorageKey.PASS_USERNAME);
                mobile = sessionStorage.getItem(webStorageKey.PASS_MOBILE);
                idCard = sessionStorage.getItem(webStorageKey.PASS_ID_CARD);
                realName = sessionStorage.getItem(webStorageKey.PASS_REAL_NAME);
                sex = sessionStorage.getItem(webStorageKey.PASS_SEX);
            }
            if (school !== null) {
                passParam.school = school;
            }

            if (college !== null) {
                passParam.college = college;
            }

            if (department !== null) {
                passParam.department = department;
            }

            if (post !== null) {
                passParam.post = post;
            }

            if (staffNumber !== null) {
                passParam.staffNumber = staffNumber;
            }

            if (username !== null) {
                passParam.username = username;
            }

            if (mobile !== null) {
                passParam.mobile = mobile;
            }

            if (idCard !== null) {
                passParam.idCard = idCard;
            }

            if (realName !== null) {
                passParam.realName = realName;
            }

            if (sex !== null) {
                passParam.sex = sex;
            }
        }

        /*
        初始化搜索框
        */
        function initPassSearchInput() {
            var school = null;
            var college = null;
            var department = null;
            var post = null;
            var staffNumber = null;
            var username = null;
            var mobile = null;
            var idCard = null;
            var realName = null;
            var sex = null;
            if (typeof(Storage) !== "undefined") {
                school = sessionStorage.getItem(webStorageKey.PASS_SCHOOL);
                college = sessionStorage.getItem(webStorageKey.PASS_COLLEGE);
                department = sessionStorage.getItem(webStorageKey.PASS_DEPARTMENT);
                post = sessionStorage.getItem(webStorageKey.PASS_POST);
                staffNumber = sessionStorage.getItem(webStorageKey.PASS_STAFF_NUMBER);
                username = sessionStorage.getItem(webStorageKey.PASS_USERNAME);
                mobile = sessionStorage.getItem(webStorageKey.PASS_MOBILE);
                idCard = sessionStorage.getItem(webStorageKey.PASS_ID_CARD);
                realName = sessionStorage.getItem(webStorageKey.PASS_REAL_NAME);
                sex = sessionStorage.getItem(webStorageKey.PASS_SEX);
            }
            if (school !== null) {
                $(getPassParamId().school).val(school);
            }

            if (college !== null) {
                $(getPassParamId().college).val(college);
            }

            if (department !== null) {
                $(getPassParamId().department).val(department);
            }

            if (post !== null) {
                $(getPassParamId().post).val(post);
            }

            if (staffNumber !== null) {
                $(getPassParamId().staffNumber).val(staffNumber);
            }

            if (username !== null) {
                $(getPassParamId().username).val(username);
            }

            if (mobile !== null) {
                $(getPassParamId().mobile).val(mobile);
            }

            if (idCard !== null) {
                $(getPassParamId().idCard).val(idCard);
            }

            if (realName !== null) {
                $(getPassParamId().realName).val(realName);
            }

            if (sex !== null) {
                $(getPassParamId().sex).val(sex);
            }
        }

        /**
         * clean pass tab param.
         */
        function cleanPassParam() {
            $(getPassParamId().school).val('');
            $(getPassParamId().college).val('');
            $(getPassParamId().department).val('');
            $(getPassParamId().post).val('');
            $(getPassParamId().staffNumber).val('');
            $(getPassParamId().username).val('');
            $(getPassParamId().mobile).val('');
            $(getPassParamId().idCard).val('');
            $(getPassParamId().realName).val('');
            var childrens = $(getPassParamId().sex).children();
            for (var i = 0; i < childrens.length; i++) {
                if ($(childrens[i]).val() === '') {
                    $(childrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        // pass tab table.
        var passTable = null;

        $(getPassParamId().school).keyup(function (event) {
            if (event.keyCode == 13) {
                initPassParam();
                passTable.ajax.reload();
            }
        });

        $(getPassParamId().college).keyup(function (event) {
            if (event.keyCode == 13) {
                initPassParam();
                passTable.ajax.reload();
            }
        });

        $(getPassParamId().department).keyup(function (event) {
            if (event.keyCode == 13) {
                initPassParam();
                passTable.ajax.reload();
            }
        });

        $(getPassParamId().post).keyup(function (event) {
            if (event.keyCode == 13) {
                initPassParam();
                passTable.ajax.reload();
            }
        });

        $(getPassParamId().staffNumber).keyup(function (event) {
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

        $(getPassParamId().idCard).keyup(function (event) {
            if (event.keyCode == 13) {
                initPassParam();
                passTable.ajax.reload();
            }
        });

        $(getPassParamId().realName).keyup(function (event) {
            if (event.keyCode == 13) {
                initPassParam();
                passTable.ajax.reload();
            }
        });

        $(getPassParamId().sex).change(function () {
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
                "aaSorting": [[2, 'desc']],// 排序
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
                    {"data": "staffNumber"},
                    {"data": "username"},
                    {"data": "mobile"},
                    {"data": "idCard"},
                    {"data": "roleName"},
                    {"data": "schoolName"},
                    {"data": "collegeName"},
                    {"data": "departmentName"},
                    {"data": "academicTitleName"},
                    {"data": "post"},
                    {"data": "sex"},
                    {"data": "birthday"},
                    {"data": "nationName"},
                    {"data": "politicalLandscapeName"},
                    {"data": "familyResidence"},
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
                        targets: 6,
                        orderable: false
                    },
                    {
                        targets: 17,
                        render: function (a, b, c, d) {
                            if (c.enabled == 0 || c.enabled == null) {
                                return "<span class='text-danger'>已注销</span>";
                            }
                            return "<span class='text-info'>正常</span>";
                        }
                    },
                    {
                        targets: 20,
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
                school: '#wait_search_school',
                college: '#wait_search_college',
                department: '#wait_search_department',
                mobile: '#wait_search_mobile',
                staffNumber: '#wait_search_staff_number',
                username: '#wait_search_username'
            };
        }

        /*
         参数
         */
        var waitParam = {
            school: '',
            college: '',
            department: '',
            mobile: '',
            staffNumber: '',
            username: ''
        };

        /*
         得到参数
         */
        function getWaitParam() {
            return waitParam;
        }

        function initWaitParam() {
            waitParam.school = $(getWaitParamId().school).val();
            waitParam.college = $(getWaitParamId().college).val();
            waitParam.department = $(getWaitParamId().department).val();
            waitParam.mobile = $(getWaitParamId().mobile).val();
            waitParam.staffNumber = $(getWaitParamId().staffNumber).val();
            waitParam.username = $(getWaitParamId().username).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.WAIT_SCHOOL, waitParam.school);
                sessionStorage.setItem(webStorageKey.WAIT_COLLEGE, waitParam.college);
                sessionStorage.setItem(webStorageKey.WAIT_DEPARTMENT, waitParam.department);
                sessionStorage.setItem(webStorageKey.WAIT_MOBILE, waitParam.mobile);
                sessionStorage.setItem(webStorageKey.WAIT_STAFF_NUMBER, waitParam.staffNumber);
                sessionStorage.setItem(webStorageKey.WAIT_USERNAME, waitParam.username);
            }
        }

        /*
        初始化搜索内容
        */
        function initWaitSearchContent() {
            var school = null;
            var college = null;
            var department = null;
            var mobile = null;
            var staffNumber = null;
            var username = null;
            if (typeof(Storage) !== "undefined") {
                school = sessionStorage.getItem(webStorageKey.WAIT_SCHOOL);
                college = sessionStorage.getItem(webStorageKey.WAIT_COLLEGE);
                department = sessionStorage.getItem(webStorageKey.WAIT_DEPARTMENT);
                mobile = sessionStorage.getItem(webStorageKey.WAIT_MOBILE);
                staffNumber = sessionStorage.getItem(webStorageKey.WAIT_STAFF_NUMBER);
                username = sessionStorage.getItem(webStorageKey.WAIT_USERNAME);
            }
            if (school !== null) {
                waitParam.school = school;
            }

            if (college !== null) {
                waitParam.college = college;
            }

            if (department !== null) {
                waitParam.department = department;
            }

            if (mobile !== null) {
                waitParam.mobile = mobile;
            }

            if (staffNumber !== null) {
                waitParam.staffNumber = staffNumber;
            }

            if (username !== null) {
                waitParam.username = username;
            }
        }

        /*
        初始化搜索框
        */
        function initWaitSearchInput() {
            var school = null;
            var college = null;
            var department = null;
            var mobile = null;
            var staffNumber = null;
            var username = null;
            if (typeof(Storage) !== "undefined") {
                school = sessionStorage.getItem(webStorageKey.WAIT_SCHOOL);
                college = sessionStorage.getItem(webStorageKey.WAIT_COLLEGE);
                department = sessionStorage.getItem(webStorageKey.WAIT_DEPARTMENT);
                mobile = sessionStorage.getItem(webStorageKey.WAIT_MOBILE);
                staffNumber = sessionStorage.getItem(webStorageKey.WAIT_STAFF_NUMBER);
                username = sessionStorage.getItem(webStorageKey.WAIT_USERNAME);
            }
            if (school !== null) {
                $(getWaitParamId().school).val(school);
            }

            if (college !== null) {
                $(getWaitParamId().college).val(college);
            }

            if (department !== null) {
                $(getWaitParamId().department).val(department);
            }

            if (mobile !== null) {
                $(getWaitParamId().mobile).val(mobile);
            }

            if (staffNumber !== null) {
                $(getWaitParamId().staffNumber).val(staffNumber);
            }

            if (username !== null) {
                $(getWaitParamId().username).val(username);
            }
        }

        function cleanWaitParam() {
            $(getWaitParamId().school).val('');
            $(getWaitParamId().college).val('');
            $(getWaitParamId().department).val('');
            $(getWaitParamId().mobile).val('');
            $(getWaitParamId().staffNumber).val('');
            $(getWaitParamId().username).val('');
        }

        var waitTable = null;

        $(getWaitParamId().school).keyup(function (event) {
            if (event.keyCode == 13) {
                initWaitParam();
                waitTable.ajax.reload();
            }
        });

        $(getWaitParamId().college).keyup(function (event) {
            if (event.keyCode == 13) {
                initWaitParam();
                waitTable.ajax.reload();
            }
        });

        $(getWaitParamId().department).keyup(function (event) {
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

        $(getWaitParamId().staffNumber).keyup(function (event) {
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
                "aaSorting": [[9, 'desc']],// 排序
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
                    {"data": "staffNumber"},
                    {"data": "username"},
                    {"data": "mobile"},
                    {"data": "schoolName"},
                    {"data": "collegeName"},
                    {"data": "departmentName"},
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
                        targets: 10,
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