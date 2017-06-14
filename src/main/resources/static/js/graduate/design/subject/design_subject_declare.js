/**
 * Created by zbeboy on 2017/6/7.
 */
require(["jquery", "handlebars", "constants", "nav_active", "bootstrap-select-zh-CN", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, constants, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/subject/declare/data',
                teachers: '/web/graduate/design/subject/teachers',
                subject_type: '/user/subject/types',
                subject_origin_type: '/user/subject/origin_types',
                declare_basic: '/web/graduate/design/subject/declare/basic',
                declare_basic_peoples: '/web/graduate/design/subject/declare/basic/peoples',
                update_title: '/web/graduate/design/subject/declare/edit/title',
                edit: '/web/graduate/design/subject/declare/edit/apply',
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
                                    }
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
                tableElement.delegate('.update_title', "click", function () {
                    updateTitle($(this).attr('data-id'));
                });

                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
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
            $(getParamId().staffId).val('');
            $(getParamId().presubjectTitle).val('');
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().organize).val('');
            $(getParamId().subjectType).val('');
            $(getParamId().originType).val('');
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

        $('#refresh').click(function () {
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
         编辑页面
         */
        function updateTitle(graduationDesignPresubjectId) {
            $.address.value(getAjaxUrl().update_title + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPresubjectId=' + graduationDesignPresubjectId + '&staffId=' + init_page_param.staffId );
        }

        /**
         * 编辑
         * @param graduationDesignPresubjectId
         */
        function edit(graduationDesignPresubjectId){
            $.address.value(getAjaxUrl().edit + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPresubjectId=' + graduationDesignPresubjectId );
        }

    });