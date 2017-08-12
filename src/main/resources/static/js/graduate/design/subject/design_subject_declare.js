/**
 * Created by zbeboy on 2017/6/7.
 */
require(["jquery", "handlebars", "constants", "nav_active", "moment", "bootstrap-select-zh-CN", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, constants, nav_active, moment) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/subject/declare/data',
                teachers: '/anyone/graduate/design/subject/teachers',
                subject_type: '/user/graduate/design/subject/types',
                subject_origin_type: '/user/graduate/design/subject/origin_types',
                declare_basic: '/web/graduate/design/subject/declare/basic',
                declare_basic_peoples: '/web/graduate/design/subject/declare/basic/peoples',
                look_url: '/web/graduate/design/subject/list/look',
                update_title: '/web/graduate/design/subject/declare/edit/title',
                edit: '/web/graduate/design/subject/declare/edit/apply',
                ok_apply: '/web/graduate/design/subject/declare/apply/ok',
                all_settings: '/web/graduate/design/subject/declare/edit/all',
                operator_condition: '/web/graduate/design/subject/declare/operator/condition',
                export_data_url: '/web/graduate/design/subject/declare/data/export',
                back: '/web/menu/graduate/design/subject'
            };
        }

        /*
         参数id
         */
        function getParamId() {
            return {
                staffId: '#select_staff',
                presubjectTitle: '#search_presubject_title',
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                organize: '#search_organize',
                subjectType: '#subject_type',
                originType: '#origin_type'
            };
        }

        /*
         参数
         */
        var param = {
            staffId: init_page_param.staffId,
            presubjectTitle: '',
            studentName: '',
            studentNumber: '',
            organize: '',
            subjectType: '',
            originType: ''
        };

        /*
         得到参数
         */
        function getParam() {
            return param;
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().back);

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(getAjaxUrl().back);
        });

        var pageAop = $('#dataContent');

        // 预编译模板
        var template = Handlebars.compile($("#operator_button").html());

        // datatables 初始化
        var responsiveHelper = undefined;
        var breakpointDefinition = {
            tablet: 1024,
            phone: 480
        };
        var tableElement = $('#example');

        var myTable = tableElement.DataTable({
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
                $('#checkall').prop('checked', false);
                // 调用全选插件
                $.fn.check({checkall_name: "checkall", checkbox_name: "check"});
                $('[data-toggle="tooltip"]').tooltip();
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[16, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                    d.staffId = getParam().staffId;
                }
            },
            "columns": [
                {"data": null},
                {"data": "presubjectTitle"},
                {"data": "subjectTypeName"},
                {"data": "originTypeName"},
                {"data": "isNewSubject"},
                {"data": "isNewTeacherMake"},
                {"data": "isNewSubjectMake"},
                {"data": "isOldSubjectChange"},
                {"data": "oldSubjectUsesTimes"},
                {"data": "planPeriod"},
                {"data": "staffName"},
                {"data": "academicTitleName"},
                {"data": "assistantTeacher"},
                {"data": "assistantTeacherAcademic"},
                {"data": "guideTimes"},
                {"data": "guidePeoples"},
                {"data": "studentNumber"},
                {"data": "studentName"},
                {"data": "organizeName"},
                {"data": "isOkApply"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.graduationDesignPresubjectId + '" name="check"/>';
                    }
                },
                {
                    targets: 1,
                    render: function (a, b, c, d) {
                        var v = '';
                        var html = '';
                        if (c.presubjectTitle !== null) {
                            if (c.presubjectTitle.length > 12) {
                                v = c.presubjectTitle.substring(0, 12) + '...';
                            } else {
                                v = c.presubjectTitle;
                            }
                            html = '<button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="top" title="' + c.presubjectTitle + '">' + v + '</button>';
                        }
                        return html;
                    }
                },
                {
                    targets: 4,
                    render: function (a, b, c, d) {
                        return c.isNewSubject === 1 ? '是' : '否';
                    }
                },
                {
                    targets: 5,
                    render: function (a, b, c, d) {
                        return c.isNewTeacherMake === 1 ? '是' : '否';
                    }
                },
                {
                    targets: 6,
                    render: function (a, b, c, d) {
                        return c.isNewSubjectMake === 1 ? '是' : '否';
                    }
                },
                {
                    targets: 7,
                    render: function (a, b, c, d) {
                        return c.isOldSubjectChange === 1 ? '是' : '否';
                    }
                },
                {
                    targets: 19,
                    render: function (a, b, c, d) {
                        var successApply = '<span class="text-info">已申报</span>';
                        var failApply = '<span class="text-danger">未申报</span>';
                        return c.isOkApply === 1 ? successApply : failApply;
                    }
                },
                {
                    targets: 20,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context = null;
                        var html = '<i class="fa fa-lock"></i>';

                        // 当前用户角色为系统或管理员
                        if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
                            init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                            // 未确认申报
                            if (c.isOkApply != 1) {
                                context =
                                    {
                                        func: [
                                            {
                                                "name": "查看",
                                                "css": "look",
                                                "type": "info",
                                                "id": c.graduationDesignPresubjectId
                                            },
                                            {
                                                "name": "修改题目",
                                                "css": "update_title",
                                                "type": "info",
                                                "id": c.graduationDesignPresubjectId
                                            },
                                            {
                                                "name": "编辑",
                                                "css": "edit",
                                                "type": "primary",
                                                "id": c.graduationDesignPresubjectId
                                            },
                                            {
                                                "name": "确认申报",
                                                "css": "ok_apply",
                                                "type": "warning",
                                                "id": c.graduationDesignPresubjectId
                                            }
                                        ]
                                    };
                            } else {
                                context =
                                    {
                                        func: [
                                            {
                                                "name": "查看",
                                                "css": "look",
                                                "type": "info",
                                                "id": c.graduationDesignPresubjectId
                                            }
                                        ]
                                    };
                            }
                        } else {
                            // 学生
                            if (init_page_param.usersTypeName === constants.global_users_type.student_type) {
                                if (c.studentId == init_page_param.studentId && init_page_param.studentId != 0) {
                                    // 未确认申报
                                    if (c.isOkApply != 1) {
                                        context =
                                            {
                                                func: [
                                                    {
                                                        "name": "修改题目",
                                                        "css": "update_title",
                                                        "type": "info",
                                                        "id": c.graduationDesignPresubjectId
                                                    }
                                                ]
                                            };
                                    } else {
                                        context =
                                            {
                                                func: [
                                                    {
                                                        "name": "查看",
                                                        "css": "look",
                                                        "type": "info",
                                                        "id": c.graduationDesignPresubjectId
                                                    }
                                                ]
                                            };
                                    }
                                } else {
                                    if (c.publicLevel == 2) {
                                        // 毕业时间结束后可查看
                                        if (moment().isAfter(init_page_param.endTime)) {
                                            context =
                                                {
                                                    func: [
                                                        {
                                                            "name": "查看",
                                                            "css": "look",
                                                            "type": "info",
                                                            "id": c.graduationDesignPresubjectId
                                                        }
                                                    ]
                                                };
                                        }
                                    } else if (c.publicLevel == 3) {
                                        // 随时可查看
                                        context =
                                            {
                                                func: [
                                                    {
                                                        "name": "查看",
                                                        "css": "look",
                                                        "type": "info",
                                                        "id": c.graduationDesignPresubjectId
                                                    }
                                                ]
                                            };
                                    }
                                }
                            } else if (init_page_param.usersTypeName === constants.global_users_type.staff_type) {// 教师
                                if (c.staffId == init_page_param.staffId && init_page_param.staffId != 0) {
                                    // 未确认申报
                                    if (c.isOkApply != 1) {
                                        context =
                                            {
                                                func: [
                                                    {
                                                        "name": "查看",
                                                        "css": "look",
                                                        "type": "info",
                                                        "id": c.graduationDesignPresubjectId
                                                    },
                                                    {
                                                        "name": "修改题目",
                                                        "css": "update_title",
                                                        "type": "info",
                                                        "id": c.graduationDesignPresubjectId
                                                    },
                                                    {
                                                        "name": "编辑",
                                                        "css": "edit",
                                                        "type": "primary",
                                                        "id": c.graduationDesignPresubjectId
                                                    },
                                                    {
                                                        "name": "确认申报",
                                                        "css": "ok_apply",
                                                        "type": "warning",
                                                        "id": c.graduationDesignPresubjectId
                                                    }
                                                ]
                                            };
                                    } else {
                                        context =
                                            {
                                                func: [
                                                    {
                                                        "name": "查看",
                                                        "css": "look",
                                                        "type": "info",
                                                        "id": c.graduationDesignPresubjectId
                                                    }
                                                ]
                                            };
                                    }
                                } else {
                                    context =
                                        {
                                            func: [
                                                {
                                                    "name": "查看",
                                                    "css": "look",
                                                    "type": "info",
                                                    "id": c.graduationDesignPresubjectId
                                                }
                                            ]
                                        };
                                }
                            }
                        }
                        if (context != null) {
                            html = template(context);
                        }
                        return html;
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-5'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.look', "click", function () {
                    look($(this).attr('data-id'));
                });

                tableElement.delegate('.update_title', "click", function () {
                    updateTitle($(this).attr('data-id'));
                });

                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
                });

                tableElement.delegate('.ok_apply', "click", function () {
                    title_apply($(this).attr('data-id'));
                });
            }
        });

        /**
         * 初始化按钮
         */
        function initGlobalButton() {
            var global_button = '';

            // 当前用户角色为系统或管理员
            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
                init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                global_button = '<button type="button" id="all_edit" class="btn btn-outline btn-default btn-sm"><i class="fa fa-tags"></i>统一设置</button>' +
                    '  <button type="button" id="all_apply" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-unlock-alt"></i>批量确认</button>';
            } else {
                if (init_page_param.usersTypeName === constants.global_users_type.staff_type) {// 教师
                    if (init_page_param.staffId === Number(getParam().staffId)) {
                        global_button = '<button type="button" id="all_edit" class="btn btn-outline btn-default btn-sm"><i class="fa fa-tags"></i>统一设置</button>' +
                            '  <button type="button" id="all_apply" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-unlock-alt"></i>批量确认</button>';
                    }
                }
            }

            global_button = global_button +
                '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
            $('#global_button').html(global_button);
        }


        /*
         初始化参数
         */
        function initParam() {
            param.staffId = $(getParamId().staffId).val();
            param.presubjectTitle = $(getParamId().presubjectTitle).val();
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.organize = $(getParamId().organize).val();
            param.subjectType = $(getParamId().subjectType).val();
            param.originType = $(getParamId().originType).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().presubjectTitle).val('');
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().organize).val('');
            $(getParamId().subjectType).val(0);
            $(getParamId().originType).val(0);
        }

        $(getParamId().staffId).on('changed.bs.select', function (e) {
            initParam();
            initDeclareBasicPeoples();
            initGlobalButton();
            myTable.ajax.reload();
        });

        $(getParamId().presubjectTitle).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().studentName).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().studentNumber).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().organize).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().subjectType).change(function () {
            initParam();
            myTable.ajax.reload();
        });

        $(getParamId().originType).change(function () {
            initParam();
            myTable.ajax.reload();
        });

        $('#search').click(function () {
            initParam();
            myTable.ajax.reload();
        });

        $('#reset_search').click(function () {
            cleanParam();
            initParam();
            myTable.ajax.reload();
        });

        pageAop.delegate('#refresh', "click", function () {
            myTable.ajax.reload();
        });

        init();

        function init() {
            initTeachers();
            initSubjectType();
            initSubjectOriginType();
            initDeclareBasic();
            initDeclareBasicPeoples();
            initGlobalButton();
        }

        /*
         初始化选中教师
         */
        var selectedTeacherCount = true;

        /**
         * 初始化教师数据
         */
        function initTeachers() {
            $.get(web_path + getAjaxUrl().teachers, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    teacherData(data);
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
         * 初始题目类型数据
         */
        function initSubjectType() {
            $.get(web_path + getAjaxUrl().subject_type, function (data) {
                if (data.state) {
                    subjectTypeData(data);
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
         * 初始化课题来源数据
         */
        function initSubjectOriginType() {
            $.get(web_path + getAjaxUrl().subject_origin_type, function (data) {
                if (data.state) {
                    subjectOriginTypeData(data);
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
         * 初始化基础信息
         */
        function initDeclareBasic() {
            $.get(web_path + getAjaxUrl().declare_basic, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    declareBasicData(data);
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
         * 初始化指导人数
         */
        function initDeclareBasicPeoples() {
            $.get(web_path + getAjaxUrl().declare_basic_peoples, {
                id: init_page_param.graduationDesignReleaseId,
                staffId: getParam().staffId
            }, function (data) {
                if (data.state) {
                    declareBasicPeoplesData(data);
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
         * 教师数据
         * @param data 数据
         */
        function teacherData(data) {
            var template = Handlebars.compile($("#staff-template").html());
            Handlebars.registerHelper('staff_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.staffId));
            });
            Handlebars.registerHelper('staff_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName + ' ' + this.staffMobile));
            });
            $(getParamId().staffId).html(template(data));

            // 只在页面初始化加载一次
            if (selectedTeacherCount) {
                selectedTeacher();
                selectedTeacherCount = false;
            }
        }

        /**
         * 选中教师
         */
        function selectedTeacher() {
            var realStaffId = init_page_param.staffId;
            var staffChildrens = $(getParamId().staffId).children();
            for (var i = 0; i < staffChildrens.length; i++) {
                if (Number($(staffChildrens[i]).val()) === realStaffId) {
                    $(staffChildrens[i]).prop('selected', true);
                    break;
                }
            }

            // 选择教师
            $(getParamId().staffId).selectpicker({
                liveSearch: true
            });
        }

        /**
         * 题目类型数据
         * @param data 数据
         */
        function subjectTypeData(data) {
            var template = Handlebars.compile($("#subject-type-template").html());
            $(getParamId().subjectType).html(template(data));
        }

        /**
         * 题目来源数据
         * @param data 数据
         */
        function subjectOriginTypeData(data) {
            var template = Handlebars.compile($("#origin-type-template").html());
            $(getParamId().originType).html(template(data));
        }

        /**
         * 基础信息
         * @param data 数据
         */
        function declareBasicData(data) {
            $('#graduationDate').text(isNull(data.objectResult.graduationDate));
            $('#departmentName').text(isNull(data.objectResult.departmentName));
            $('#scienceName').text(isNull(data.objectResult.scienceName));
            $('#organizeNames').text(isNull(data.objectResult.organizeNames));
            $('#organizePeoples').text(isNull(data.objectResult.organizePeoples));
        }

        /**
         * 指导人数
         * @param data 数据
         */
        function declareBasicPeoplesData(data) {
            $('#guidePeoples').text(isNull(data.objectResult));
        }

        /**
         * 空值处理
         * @param param
         * @returns {string}
         */
        function isNull(param) {
            return param == null ? "" : param;
        }

        /*
         查看页面
         */
        function look(graduationDesignPresubjectId) {
            $.address.value(getAjaxUrl().look_url + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPresubjectId=' + graduationDesignPresubjectId);
        }

        /*
         编辑页面
         */
        function updateTitle(graduationDesignPresubjectId) {
            $.post(getAjaxUrl().operator_condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    $.address.value(getAjaxUrl().update_title + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPresubjectId=' + graduationDesignPresubjectId + '&staffId=' + init_page_param.staffId);
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
         * 编辑
         * @param graduationDesignPresubjectId
         */
        function edit(graduationDesignPresubjectId) {
            $.post(getAjaxUrl().operator_condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    $.address.value(getAjaxUrl().edit + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPresubjectId=' + graduationDesignPresubjectId);
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
         * 申报
         * @param graduationDesignPresubjectId
         */
        function title_apply(graduationDesignPresubjectId) {
            var msg;
            msg = Messenger().post({
                message: "确定申报该题目吗，申报后将不可再编辑?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            apply(graduationDesignPresubjectId);
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

        function apply(graduationDesignPresubjectId) {
            $.post(getAjaxUrl().operator_condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    sendApplyAjax(graduationDesignPresubjectId);
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
         批量确认
         */
        pageAop.delegate('#all_apply', "click", function () {
            var graduationDesignPresubjectIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                graduationDesignPresubjectIds.push($(ids[i]).val());
            }

            if (graduationDesignPresubjectIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定申报选中的题目吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                applies(graduationDesignPresubjectIds);
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
                Messenger().post("未发现有选中的题目!");
            }
        });

        function applies(graduationDesignPresubjectIds) {
            $.post(getAjaxUrl().operator_condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    sendApplyAjax(graduationDesignPresubjectIds.join(","));
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
         * 申报ajax
         * @param graduationDesignPresubjectId
         */
        function sendApplyAjax(graduationDesignPresubjectId) {
            Messenger().run({
                successMessage: '确认申报成功',
                errorMessage: '确认申报失败',
                progressMessage: '正在确认中....'
            }, {
                url: web_path + getAjaxUrl().ok_apply,
                type: 'post',
                data: {
                    graduationDesignPresubjectIds: graduationDesignPresubjectId,
                    id: init_page_param.graduationDesignReleaseId,
                    staffId: init_page_param.staffId
                },
                success: function (data) {
                    if (data.state) {
                        myTable.ajax.reload();
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

        /*
         统一设置
         */
        pageAop.delegate('#all_edit', "click", function () {
            console.log('hahahah');
            $.post(getAjaxUrl().operator_condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    sendAllSettingsAjax();
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        });

        /**
         * 统一设置ajax
         */
        function sendAllSettingsAjax() {
            if (getParam().staffId > 0) {
                $.address.value(getAjaxUrl().all_settings + '?id=' + init_page_param.graduationDesignReleaseId + '&staffId=' + getParam().staffId);
            } else {
                Messenger().post({
                    message: '请选择指导教师',
                    type: 'error',
                    showCloseButton: true
                });
            }
        }

        $('#export_xls').click(function () {
            initParam();
            if (getParam().staffId > 0) {
                var searchParam = JSON.stringify(getParam());
                var exportFile = {
                    fileName: $('#export_file_name').val(),
                    ext: 'xls'
                };
                var graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                window.location.href = web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&graduationDesignReleaseId=" + graduationDesignReleaseId + '&staffId=' + getParam().staffId;
            } else {
                Messenger().post({
                    message: '请选择指导教师',
                    type: 'error',
                    showCloseButton: true
                });
            }

        });

        $('#export_xlsx').click(function () {
            initParam();
            if (getParam().staffId > 0) {
                var searchParam = JSON.stringify(getParam());
                var exportFile = {
                    fileName: $('#export_file_name').val(),
                    ext: 'xlsx'
                };
                var graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                window.location.href = web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&graduationDesignReleaseId=" + graduationDesignReleaseId + '&staffId=' + getParam().staffId;
            } else {
                Messenger().post({
                    message: '请选择指导教师',
                    type: 'error',
                    showCloseButton: true
                });
            }

        });

    });