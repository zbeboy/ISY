/**
 * Created by lenovo on 2016/12/14.
 */
//# sourceURL=internship_journal_list.js
require(["jquery", "handlebars", "constants", "nav_active", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, constants, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/internship/journal/list/data',
                del: '/web/internship/journal/list/del',
                edit: '/web/internship/journal/list/edit',
                look: '/web/internship/journal/list/look',
                download: '/web/internship/journal/list/download',
                nav: '/web/menu/internship/journal',
                add: '/web/internship/journal/list/add',
                valid_is_student: '/anyone/valid/cur/is/student',
                valid_student: '/web/internship/journal/valid/student',
                back: '/web/menu/internship/journal'
            };
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().nav);

        /*
         检验id
         */
        var validId = {
            student: '#valid_student'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            student: '#student_error_msg'
        };

        /**
         * 检验失败
         * @param validId
         * @param errorMsgId
         * @param msg
         */
        function validErrorDom(validId, errorMsgId, msg) {
            $(validId).addClass('has-error').removeClass('has-success');
            $(errorMsgId).removeClass('hidden').text(msg);
        }

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
            "aaSorting": [[5, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.internshipReleaseId = init_page_param.internshipReleaseId;
                }
            },
            "columns": [
                {"data": null},
                {"data": "studentName"},
                {"data": "studentNumber"},
                {"data": "organize"},
                {"data": "schoolGuidanceTeacher"},
                {"data": "createDateStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.internshipJournalId + '" name="check"/>';
                    }
                },
                {
                    targets: 6,
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
                                        "id": c.internshipJournalId
                                    },
                                    {
                                        "name": "编辑",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.internshipJournalId
                                    },
                                    {
                                        "name": "删除",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.internshipJournalId
                                    },
                                    {
                                        "name": "下载",
                                        "css": "download",
                                        "type": "default",
                                        "id": c.internshipJournalId
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
                                            "id": c.internshipJournalId
                                        },
                                        {
                                            "name": "编辑",
                                            "css": "edit",
                                            "type": "primary",
                                            "id": c.internshipJournalId
                                        },
                                        {
                                            "name": "删除",
                                            "css": "del",
                                            "type": "danger",
                                            "id": c.internshipJournalId
                                        },
                                        {
                                            "name": "下载",
                                            "css": "download",
                                            "type": "default",
                                            "id": c.internshipJournalId
                                        }
                                    ]
                                };
                            } else {// 非作者也非管理员
                                // 若限制仅允许教职工查阅
                                if (c.isSeeStaff == 1) {
                                    if (init_page_param.usersTypeName === constants.global_users_type.staff_type) {
                                        context =
                                        {
                                            func: [
                                                {
                                                    "name": "查看",
                                                    "css": "look",
                                                    "type": "info",
                                                    "id": c.internshipJournalId
                                                },
                                                {
                                                    "name": "下载",
                                                    "css": "download",
                                                    "type": "default",
                                                    "id": c.internshipJournalId
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
                                                "id": c.internshipJournalId
                                            },
                                            {
                                                "name": "下载",
                                                "css": "download",
                                                "type": "default",
                                                "id": c.internshipJournalId
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

                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
                });

                tableElement.delegate('.del', "click", function () {
                    journal_del($(this).attr('data-id'));
                });

                tableElement.delegate('.download', "click", function () {
                    download($(this).attr('data-id'));
                });
            }
        });

        var global_button = '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
            init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
            var temp = '  <button type="button" id="journal_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量删除</button>';
            global_button = temp + global_button;
        }
        global_button = '<button type="button" id="journal_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            global_button;
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                organize: '#search_organize',
                guidanceTeacher: '#search_guidance_teacher'
            };
        }

        /*
         参数
         */
        var param = {
            studentName: '',
            studentNumber: '',
            organize: '',
            guidanceTeacher: ''
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
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.organize = $(getParamId().organize).val();
            param.guidanceTeacher = $(getParamId().guidanceTeacher).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().organize).val('');
            $(getParamId().guidanceTeacher).val('');
        }

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

        $(getParamId().guidanceTeacher).keyup(function (event) {
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
         添加
         */
        $('#journal_add').click(function () {
            var id = init_page_param.internshipReleaseId;
            // 如果用户类型不是学生，则这里需要一个弹窗，填写学生账号或学生学号以获取学生id
            $.get(web_path + getAjaxUrl().valid_is_student, function (data) {
                if (data.state) {
                    accessAdd(id, data.objectResult);
                } else {
                    $('#studentInfoInternshipReleaseId').val(id);
                    $('#studentModal').modal('show');
                }
            });
        });

        /*
         学生 form 确定
         */
        $('#studentInfo').click(function () {
            validStudent();
        });

        // 用于可以去添加页
        var to_add = false;
        var to_add_data = '';

        $('#studentModal').on('hidden.bs.modal', function (e) {
            // do something...
            if (to_add) {
                to_add = false;
                accessAdd($('#studentInfoInternshipReleaseId').val(), to_add_data);
            }
        });

        /**
         * 检验学生信息
         */
        function validStudent() {
            var studentUsername = $('#studentUsername').val();
            var studentNumber = $('#studentNumber').val();
            if (studentUsername.length <= 0 && studentNumber.length <= 0) {
                validErrorDom(validId.student, errorMsgId.student, '请至少填写一项学生信息');
            } else {
                var student = "";
                var type = -1;
                if (studentUsername.length > 0) {
                    student = studentUsername;
                    type = 0;
                }

                if (studentNumber.length > 0) {
                    student = studentNumber;
                    type = 1;
                }

                // 检验学生信息
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().valid_student,
                    type: 'post',
                    data: {student: student, internshipReleaseId: init_page_param.internshipReleaseId, type: type},
                    success: function (data) {
                        if (data.state) {
                            to_add_data = data.objectResult;
                            to_add = true;
                            $('#studentModal').modal('hide');
                        } else {
                            validErrorDom(validId.student, errorMsgId.student, data.msg);
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
        }

        /*
         批量删除
         */
        $('#journal_dels').click(function () {
            var journalIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                journalIds.push($(ids[i]).val());
            }
            if (journalIds.length > 0) {
                journal_dels(journalIds);
            } else {
                Messenger().post("未发现有选中的系!");
            }
        });

        /**
         * 进入添加
         * @param internshipReleaseId
         * @param studentId
         */
        function accessAdd(internshipReleaseId, studentId) {
            $.address.value(getAjaxUrl().add + '?id=' + internshipReleaseId + '&studentId=' + studentId);
        }

        /*
         查看页面
         */
        function look(journalId) {
            $.address.value(getAjaxUrl().look + '?id=' + journalId);
        }

        /*
         编辑页面
         */
        function edit(journalId) {
            $.address.value(getAjaxUrl().edit + '?id=' + journalId);
        }

        /*
         下载
         */
        function download(journalId) {
            window.location.href = web_path + getAjaxUrl().download + '?id=' + journalId;
        }

        /*
         删除
         */
        function journal_del(journalId) {
            var msg;
            msg = Messenger().post({
                message: "确定删除日志吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(journalId);
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
        function journal_dels(journalIds) {
            var msg;
            msg = Messenger().post({
                message: "确定删除选中日志吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            dels(journalIds);
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

        function del(journalId) {
            sendDelAjax(journalId, '删除');
        }

        function dels(journalIds) {
            sendDelAjax(journalIds.join(','), '批量删除');
        }

        /**
         * 删除ajax
         * @param journalId
         * @param message
         */
        function sendDelAjax(journalId, message) {
            Messenger().run({
                successMessage: message + '日志成功',
                errorMessage: message + '日志失败',
                progressMessage: '正在' + message + '日志....'
            }, {
                url: web_path + getAjaxUrl().del,
                type: 'post',
                data: {journalIds: journalId},
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
    });