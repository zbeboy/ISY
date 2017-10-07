/**
 * Created by zbeboy on 2017/5/24.
 */
require(["jquery", "nav_active", "handlebars", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, nav_active, Handlebars) {

        /*
        参数
        */
        var param = {
            studentName: '',
            studentNumber: '',
            organizeName: ''
        };

        /*
       web storage key.
       */
        var webStorageKey = {
            STUDENT_NAME: 'GRADUATE_DESIGN_ADJUSTECH_SUBMIT_STUDENT_NAME_SEARCH_' + init_page_param.graduationDesignReleaseId,
            STUDENT_NUMBER: 'GRADUATE_DESIGN_ADJUSTECH_SUBMIT_STUDENT_NUMBER_SEARCH_' + init_page_param.graduationDesignReleaseId,
            ORGANIZE_NAME: 'GRADUATE_DESIGN_ADJUSTECH_SUBMIT_ORGANIZE_NAME_SEARCH_' + init_page_param.graduationDesignReleaseId
        };

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/adjustech/student/submit/data',
                organizes: '/anyone/graduate/design/release/organizes',
                adjust_teacher: '/web/graduate/design/adjustech/teachers',
                update: '/web/graduate/design/adjustech/update',
                del: '/web/graduate/design/adjustech/delete',
                wish: '/web/graduate/design/adjustech/student/wish',
                back: '/web/menu/graduate/design/adjustech'
            };
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().back);

        init();

        function init() {
            $.get(getAjaxUrl().organizes, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                organizeData(data);
            });
        }

        function organizeData(data) {
            var template = Handlebars.compile($("#organize-template").html());

            Handlebars.registerHelper('organize_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.organizeId));
            });

            Handlebars.registerHelper('organize_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.organizeName));
            });

            $(getParamId().organizeName).html(template(data));

            var organizeName = null;
            if (typeof(Storage) !== "undefined") {
                organizeName = sessionStorage.getItem(webStorageKey.ORGANIZE_NAME);
            }
            if (organizeName !== null) {
                $(getParamId().organizeName).val(organizeName);
            }
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
            "aaSorting": [[2, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                }
            },
            "columns": [
                {"data": null},
                {"data": "studentName"},
                {"data": "studentNumber"},
                {"data": "organizeName"},
                {"data": "staffName"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.graduationDesignTutorId + '" name="check"/>';
                    }
                },
                {
                    targets: 5,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context =
                            {
                                func: [
                                    {
                                        "name": "志愿",
                                        "css": "wish",
                                        "type": "default",
                                        "id": c.graduationDesignTutorId,
                                        "studentName": c.studentName,
                                        "graduationDesignTeacherId": c.graduationDesignTeacherId
                                    },
                                    {
                                        "name": "调整",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.graduationDesignTutorId,
                                        "studentName": c.studentName,
                                        "graduationDesignTeacherId": c.graduationDesignTeacherId
                                    },
                                    {
                                        "name": "删除",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.graduationDesignTutorId,
                                        "studentName": c.studentName,
                                        "graduationDesignTeacherId": c.graduationDesignTeacherId
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-4'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.wish', "click", function () {
                    wish($(this).attr('data-id'));
                });

                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'), $(this).attr('data-teacher'));
                });

                tableElement.delegate('.del', "click", function () {
                    student_del($(this).attr('data-id'), $(this).attr('data-student'));
                });
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '<button type="button" id="student_adjust" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-retweet"></i>批量调整</button>' +
            '  <button type="button" id="student_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量删除</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                organizeName: '#select_organize'
            };
        }

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
            param.organizeName = $(getParamId().organizeName).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.STUDENT_NAME, param.studentName);
                sessionStorage.setItem(webStorageKey.STUDENT_NUMBER, param.studentNumber);
                sessionStorage.setItem(webStorageKey.ORGANIZE_NAME, param.organizeName);
            }
        }

        /*
        初始化搜索内容
        */
        function initSearchContent() {
            var studentName = null;
            var studentNumber = null;
            var organizeName = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                organizeName = sessionStorage.getItem(webStorageKey.ORGANIZE_NAME);
            }
            if (studentName !== null) {
                param.studentName = studentName;
            }

            if (studentNumber !== null) {
                param.studentNumber = studentNumber;
            }

            if (organizeName !== null) {
                param.organizeName = organizeName;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var studentName = null;
            var studentNumber = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
            }
            if (studentName !== null) {
                $(getParamId().studentName).val(studentName);
            }

            if (studentNumber !== null) {
                $(getParamId().studentNumber).val(studentNumber);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().organizeName).val(0);
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

        $(getParamId().organizeName).change(function () {
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

        /*
         批量调整
         */
        $('#student_adjust').click(function () {
            var graduationDesignTutorIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                graduationDesignTutorIds.push($(ids[i]).val());
            }
            if (graduationDesignTutorIds.length > 0) {
                adjustStudent(init_page_param.graduationDesignReleaseId, graduationDesignTutorIds.join(','), -1);
            } else {
                Messenger().post("未发现有选中的学生!");
            }
        });

        /*
         批量删除
         */
        $('#student_dels').click(function () {
            var graduationDesignTutorIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                graduationDesignTutorIds.push($(ids[i]).val());
            }
            if (graduationDesignTutorIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定删除选中的学生吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                dels(graduationDesignTutorIds);
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
                Messenger().post("未发现有选中的学生!");
            }
        });

        /*
         志愿
         */
        function wish(id) {
            $.post(getAjaxUrl().wish, {
                graduationDesignTutorId: id,
                id: init_page_param.graduationDesignReleaseId
            }, function (data) {
                if (data.state) {
                    wishData(data);
                    $('#wishModal').modal('show');
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
         调整
         */
        function edit(id, graduationDesignTeacherId) {
            adjustStudent(init_page_param.graduationDesignReleaseId, id, graduationDesignTeacherId);
        }

        /**
         * 进行调整
         * @param graduationDesignReleaseId 毕业发布id
         * @param graduationDesignTutorId 教师与学生关联表 id
         * @param graduationDesignTeacherId 指导老师id
         */
        function adjustStudent(graduationDesignReleaseId, graduationDesignTutorId, graduationDesignTeacherId) {
            $.get(getAjaxUrl().adjust_teacher, {
                id: graduationDesignReleaseId,
                graduationDesignTeacherId: graduationDesignTeacherId
            }, function (data) {
                if (data.state) {
                    teachersData(data);
                    $('#teacherGraduationDesignTutorId').val(graduationDesignTutorId);
                    $('#teacherGraduationDesignTeacherId').val(graduationDesignTeacherId);
                    $('#teacherModal').modal('show');
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
        function teachersData(data) {
            var template = Handlebars.compile($("#adjust-teacher-template").html());
            $('#teachers').html(template(data));
        }

        /**
         * 志愿数据
         * @param data 数据
         */
        function wishData(data) {
            var template = Handlebars.compile($("#wish-template").html());
            $('#wishData').html(template(data));
        }

        /**
         * 保存调整
         */
        $('#saveTeacher').click(function () {
            if ($("input[name='graduationDesignTeacherId']:checked").length > 0) {
                $.post(getAjaxUrl().update, $('#teacher_form').serialize(), function (data) {
                    if (data.state) {
                        $('#teacherModal').modal('hide');
                        $('#teacher_error_msg').addClass('hidden').removeClass('text-danger').text('');
                        myTable.ajax.reload();
                    } else {
                        $('#teacher_error_msg').removeClass('hidden').addClass('text-danger').text(data.msg);
                    }
                });
            } else {
                $('#teacher_error_msg').removeClass('hidden').addClass('text-danger').text('请选择指导教师');
            }
        });

        /*
         删除
         */
        function student_del(graduationDesignTutorId, studentName) {
            var msg;
            msg = Messenger().post({
                message: "确定删除 '" + studentName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(graduationDesignTutorId);
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

        function del(graduationDesignTutorId) {
            sendDelAjax(graduationDesignTutorId);
        }

        function dels(graduationDesignTutorIds) {
            sendDelAjax(graduationDesignTutorIds.join(","));
        }

        /**
         * 删除ajax
         * @param graduationDesignTutorId
         */
        function sendDelAjax(graduationDesignTutorId) {
            $.post(web_path + getAjaxUrl().del, {
                id: init_page_param.graduationDesignReleaseId,
                graduationDesignTutorIds: graduationDesignTutorId
            }, function (data) {
                if (data.state) {
                    myTable.ajax.reload();
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