/**
 * Created by zbeboy on 2017/6/6.
 */
require(["jquery", "handlebars", "constants", "nav_active", "moment", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, constants, nav_active, moment) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/subject/list/data',
                look_url: '/web/graduate/design/subject/list/look',
                edit_url: '/web/graduate/design/subject/list/edit',
                back: '/web/menu/graduate/design/subject'
            };
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
                $('[data-toggle="tooltip"]').tooltip();
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[4, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                }
            },
            "columns": [
                {"data": "presubjectTitle"},
                {"data": "realName"},
                {"data": "studentNumber"},
                {"data": "organizeName"},
                {"data": "updateTimeStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    render: function (a, b, c, d) {
                        var v = '';
                        if (c.presubjectTitle !== null) {
                            if(c.presubjectTitle.length > 12){
                                v = c.presubjectTitle.substring(0, 12) + '...';
                            } else {
                                v = c.presubjectTitle;
                            }
                        }
                        return '<button type="button" class="btn btn-default" data-toggle="tooltip" data-placement="top" title="' + c.presubjectTitle + '">' + v + '</button>';
                    }
                },
                {
                    targets: 5,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context = null;
                        var html = '<i class="fa fa-lock"></i>';
                        // 当前用户查看自己的实习日志
                        if (c.studentId == init_page_param.studentId && init_page_param.studentId != 0) {
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
                                        "name": "编辑",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.graduationDesignPresubjectId
                                    }
                                ]
                            };
                        } else { // 该实习日志不属于当前用户
                            // 当前用户角色为系统或管理员
                            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
                                init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
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
                                            "name": "编辑",
                                            "css": "edit",
                                            "type": "primary",
                                            "id": c.graduationDesignPresubjectId
                                        }
                                    ]
                                };
                            } else {// 非作者也非管理员
                                // 教职工可查看也可编辑
                                if (init_page_param.usersTypeName === constants.global_users_type.staff_type) {
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
                                                "name": "编辑",
                                                "css": "edit",
                                                "type": "primary",
                                                "id": c.graduationDesignPresubjectId
                                            }
                                        ]
                                    };
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

                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
                });
            }
        });

        var global_button = '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                presubjectTitle: '#search_presubject_title',
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                organize: '#search_organize'
            };
        }

        /*
         参数
         */
        var param = {
            presubjectTitle: '',
            studentName: '',
            studentNumber: '',
            organize: ''
        };

        /*
         得到参数
         */
        function getParam() {
            return param;
        }

        /*
         初始化参数
         */
        function initParam() {
            param.presubjectTitle = $(getParamId().presubjectTitle).val();
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.organize = $(getParamId().organize).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().presubjectTitle).val('');
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().organize).val('');
        }

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

        /*
         查看页面
         */
        function look(graduationDesignPresubjectId) {
            $.address.value(getAjaxUrl().look_url + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPresubjectId=' + graduationDesignPresubjectId);
        }

        /*
         编辑页面
         */
        function edit(graduationDesignPresubjectId) {
            $.address.value(getAjaxUrl().edit_url + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignPresubjectId=' + graduationDesignPresubjectId);
        }
    });