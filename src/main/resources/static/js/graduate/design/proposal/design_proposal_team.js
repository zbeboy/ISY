/**
 * Created by zbeboy on 2017/6/28.
 */
require(["jquery", "handlebars", "constants", "nav_active", "bootstrap-select-zh-CN", "datatables.responsive", "jquery.address", "messenger"],
    function ($, Handlebars, constants, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/proposal/team/data',
                teachers: '/anyone/graduate/design/subject/teachers',
                datum_type: '/use/graduate/design/proposal/datums',
                back: '/web/menu/graduate/design/proposal'
            };
        }

        /*
         参数id
         */
        function getParamId() {
            return {
                staffId: '#select_staff',
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                originalFileName: '#search_file',
                graduationDesignDatumTypeName: '#graduation_design_datum_type'
            };
        }

        /*
         参数
         */
        var param = {
            staffId: init_page_param.staffId,
            studentName: '',
            studentNumber: '',
            originalFileName: '',
            graduationDesignDatumTypeName: ''
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
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[6, 'desc']],// 排序
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
                {"data": "realName"},
                {"data": "studentNumber"},
                {"data": "organizeName"},
                {"data": "originalFileName"},
                {"data": "version"},
                {"data": "graduationDesignDatumTypeName"},
                {"data": "updateTimeStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 7,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context = null;
                        var html = '<i class="fa fa-lock"></i>';

                        // 当前用户角色为系统或管理员
                        if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
                            init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                            context =
                                {
                                    func: [
                                        {
                                            "name": "替换",
                                            "css": "edit",
                                            "type": "primary",
                                            "id": c.graduationDesignDatumId
                                        },
                                        {
                                            "name": "删除",
                                            "css": "del",
                                            "type": "danger",
                                            "id": c.graduationDesignDatumId
                                        },
                                        {
                                            "name": "下载",
                                            "css": "download",
                                            "type": "default",
                                            "id": c.graduationDesignDatumId
                                        }
                                    ]
                                };
                        } else {
                            // 学生
                            if (init_page_param.usersTypeName === constants.global_users_type.student_type) {
                                if (c.studentId == init_page_param.studentId && init_page_param.studentId != 0) {
                                    context =
                                        {
                                            func: [
                                                {
                                                    "name": "替换",
                                                    "css": "edit",
                                                    "type": "primary",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "删除",
                                                    "css": "del",
                                                    "type": "danger",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "下载",
                                                    "css": "download",
                                                    "type": "default",
                                                    "id": c.graduationDesignDatumId
                                                }
                                            ]
                                        };
                                }
                            } else if (init_page_param.usersTypeName === constants.global_users_type.staff_type) {// 教师
                                if (c.staffId == init_page_param.staffId && init_page_param.staffId != 0) {
                                    context =
                                        {
                                            func: [
                                                {
                                                    "name": "替换",
                                                    "css": "edit",
                                                    "type": "primary",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "删除",
                                                    "css": "del",
                                                    "type": "danger",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "下载",
                                                    "css": "download",
                                                    "type": "default",
                                                    "id": c.graduationDesignDatumId
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
                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
                });

                /*  tableElement.delegate('.del', "click", function () {
                 del($(this).attr('data-id'));
                 });

                 tableElement.delegate('.download', "click", function () {
                 download($(this).attr('data-id'));
                 });*/
            }
        });

        var global_button =
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').html(global_button);


        /*
         初始化参数
         */
        function initParam() {
            param.staffId = $(getParamId().staffId).val();
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.originalFileName = $(getParamId().originalFileName).val();
            param.graduationDesignDatumTypeName = $(getParamId().graduationDesignDatumTypeName).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().originalFileName).val('');
            $(getParamId().graduationDesignDatumTypeName).val(0);
        }

        $(getParamId().staffId).on('changed.bs.select', function (e) {
            initParam();
            myTable.ajax.reload();
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

        $(getParamId().originalFileName).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().graduationDesignDatumTypeName).change(function () {
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
            initDatumType();
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
         * 初始文件类型数据
         */
        function initDatumType() {
            $.get(web_path + getAjaxUrl().datum_type, function (data) {
                if (data.state) {
                    datumTypeData(data);
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
        function datumTypeData(data) {
            var template = Handlebars.compile($("#datum-type-template").html());
            $(getParamId().graduationDesignDatumTypeName).html(template(data));
        }

        /**
         * 编辑
         * @param graduationDesignDatumId
         */
        function edit(graduationDesignDatumId) {
            $.post(getAjaxUrl().operator_condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    $.address.value(getAjaxUrl().edit + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignDatumId=' + graduationDesignDatumId);
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