/**
 * Created by lenovo on 2016-12-24.
 */
//# sourceURL=internship_regulate_list.js
require(["jquery", "handlebars", "constants", "nav_active", "moment", "datatables.responsive", "check.all", "jquery.address", "messenger", "bootstrap-daterangepicker"],
    function ($, Handlebars, constants, nav_active, moment) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/internship/regulate/list/data',
                export_data_url: '/web/internship/regulate/list/data/export',
                del: '/web/internship/regulate/list/del',
                edit: '/web/internship/regulate/list/edit',
                add: '/web/internship/regulate/list/add',
                look: '/web/internship/regulate/list/look',
                valid_is_staff: '/anyone/valid/cur/is/staff',
                valid_staff: '/web/internship/regulate/valid/staff',
                nav: '/web/menu/internship/regulate',
                back: '/web/menu/internship/regulate'
            };
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().nav);

        /*
         参数id
         */
        function getParamId() {
            return {
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                schoolGuidanceTeacher: '#search_school_guidance_teacher',
                createDate: '#search_create_date'
            };
        }

        /*
         参数
         */
        var param = {
            studentName: '',
            studentNumber: '',
            schoolGuidanceTeacher: '',
            createDate: ''
        };

        /*
        web storage key.
        */
        var webStorageKey = {
            STUDENT_NAME: 'INTERNSHIP_REGULATE_LIST_STUDENT_NAME_SEARCH_' + init_page_param.internshipReleaseId,
            STUDENT_NUMBER: 'INTERNSHIP_REGULATE_LIST_STUDENT_NUMBER_SEARCH_' + init_page_param.internshipReleaseId,
            SCHOOL_GUIDANCE_TEACHER: 'INTERNSHIP_REGULATE_LIST_SCHOOL_GUIDANCE_TEACHER_SEARCH_' + init_page_param.internshipReleaseId,
            CREATE_DATE: 'INTERNSHIP_REGULATE_LIST_CREATE_DATE_SEARCH_' + init_page_param.internshipReleaseId
        };

        /*
         检验id
         */
        var validId = {
            staff: '#valid_staff'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            staff: '#staff_error_msg'
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
         得到参数
         */
        function getParam() {
            return param;
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
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.internshipReleaseId = init_page_param.internshipReleaseId;
                }
            },
            "columns": [
                {"data": null},
                {"data": "studentName"},
                {"data": "studentNumber"},
                {"data": "studentTel"},
                {"data": "schoolGuidanceTeacher"},
                {"data": "createDateStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.internshipRegulateId + '" name="check"/>';
                    }
                },
                {
                    targets: 6,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context = [];
                        if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
                            init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                            context =
                            {
                                func: [
                                    {
                                        "name": "查看",
                                        "css": "look",
                                        "type": "info",
                                        "id": c.internshipRegulateId
                                    },
                                    {
                                        "name": "编辑",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.internshipRegulateId
                                    },
                                    {
                                        "name": "删除",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.internshipRegulateId
                                    }
                                ]
                            };
                        } else {
                            if (c.staffId == init_page_param.staffId && init_page_param.staffId != 0) {
                                context =
                                {
                                    func: [
                                        {
                                            "name": "查看",
                                            "css": "look",
                                            "type": "info",
                                            "id": c.internshipRegulateId
                                        },
                                        {
                                            "name": "编辑",
                                            "css": "edit",
                                            "type": "primary",
                                            "id": c.internshipRegulateId
                                        },
                                        {
                                            "name": "删除",
                                            "css": "del",
                                            "type": "danger",
                                            "id": c.internshipRegulateId
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
                                            "id": c.internshipRegulateId
                                        }
                                    ]
                                };
                            }
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
                    regulate_del($(this).attr('data-id'));
                });
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
            init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
            var temp = '  <button type="button" id="regulate_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量删除</button>';
            global_button = temp + global_button;
        }
        global_button = '<button type="button" id="regulate_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            global_button;
        $('#global_button').append(global_button);

        /*
         初始化参数
         */
        function initParam() {
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.schoolGuidanceTeacher = $(getParamId().schoolGuidanceTeacher).val();
            param.createDate = $(getParamId().createDate).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.STUDENT_NAME, param.studentName);
                sessionStorage.setItem(webStorageKey.STUDENT_NUMBER, param.studentNumber);
                sessionStorage.setItem(webStorageKey.SCHOOL_GUIDANCE_TEACHER, param.schoolGuidanceTeacher);
                sessionStorage.setItem(webStorageKey.CREATE_DATE, param.createDate);
            }
        }

        // 创建日期
        $(getParamId().createDate).daterangepicker({
            "startDate": moment().subtract(2, "days"),
            "endDate": moment(),
            "timePicker": true,
            "timePicker24Hour": true,
            "timePickerIncrement": 30,
            "autoUpdateInput": false,
            "locale": {
                format: 'YYYY-MM-DD HH:mm:ss',
                applyLabel: '确定',
                cancelLabel: '取消',
                fromLabel: '起始时间',
                toLabel: '结束时间',
                customRangeLabel: '自定义',
                separator: ' 至 ',
                daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
                monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                    '七月', '八月', '九月', '十月', '十一月', '十二月']
            }
        }, function (start, end, label) {
            console.log('New date range selected: ' + start.format('YYYY-MM-DD HH:mm:ss') + ' to ' + end.format('YYYY-MM-DD HH:mm:ss') + ' (predefined range: ' + label + ')');
        });

        $(getParamId().createDate).on('apply.daterangepicker', function(ev, picker) {
            $(this).val(picker.startDate.format('YYYY-MM-DD HH:mm:ss') + ' 至 ' + picker.endDate.format('YYYY-MM-DD HH:mm:ss'));
            initParam();
            myTable.ajax.reload();
        });

        $(getParamId().createDate).on('cancel.daterangepicker', function(ev, picker) {
            $(this).val('');
        });

        /*
        初始化搜索内容
        */
        function initSearchContent() {
            var studentName = null;
            var studentNumber = null;
            var schoolGuidanceTeacher = null;
            var createDate = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                schoolGuidanceTeacher = sessionStorage.getItem(webStorageKey.SCHOOL_GUIDANCE_TEACHER);
                createDate = sessionStorage.getItem(webStorageKey.CREATE_DATE);
            }
            if (studentName !== null) {
                param.studentName = studentName;
            }

            if (studentNumber !== null) {
                param.studentNumber = studentNumber;
            }

            if (schoolGuidanceTeacher !== null) {
                param.schoolGuidanceTeacher = schoolGuidanceTeacher;
            }

            if (createDate !== null) {
                param.createDate = createDate;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var studentName = null;
            var studentNumber = null;
            var schoolGuidanceTeacher = null;
            var createDate = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                schoolGuidanceTeacher = sessionStorage.getItem(webStorageKey.SCHOOL_GUIDANCE_TEACHER);
                createDate = sessionStorage.getItem(webStorageKey.CREATE_DATE);
            }
            if (studentName !== null) {
                $(getParamId().studentName).val(studentName);
            }

            if (studentNumber !== null) {
                $(getParamId().studentNumber).val(studentNumber);
            }

            if (schoolGuidanceTeacher !== null) {
                $(getParamId().schoolGuidanceTeacher).val(schoolGuidanceTeacher);
            }

            if (createDate !== null) {
                $(getParamId().createDate).val(createDate);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().schoolGuidanceTeacher).val('');
            $(getParamId().createDate).val('');
        }

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

        $(getParamId().schoolGuidanceTeacher).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $('#export_xls').click(function () {
            initParam();
            var searchParam = JSON.stringify(getParam());
            var exportFile = {
                fileName: $('#export_file_name').val(),
                ext: 'xls'
            };
            var internshipReleaseId = init_page_param.internshipReleaseId;
            window.location.href = web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&internshipReleaseId=" + internshipReleaseId;
        });

        $('#export_xlsx').click(function () {
            initParam();
            var searchParam = JSON.stringify(getParam());
            var exportFile = {
                fileName: $('#export_file_name').val(),
                ext: 'xlsx'
            };
            var internshipReleaseId = init_page_param.internshipReleaseId;
            window.location.href = web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&internshipReleaseId=" + internshipReleaseId;
        });

        /*
         添加
         */
        $('#regulate_add').click(function () {
            var id = init_page_param.internshipReleaseId;
            $.get(web_path + getAjaxUrl().valid_is_staff, function (data) {
                if (data.state) {
                    accessAdd(id, data.objectResult);
                } else {
                    $('#staffInfoInternshipReleaseId').val(id);
                    $('#staffModal').modal('show');
                }
            });
        });

        /*
         教职工 form 确定
         */
        $('#staffInfo').click(function () {
            validStaff();
        });

        // 用于可以去添加页
        var to_add = false;
        var to_add_data = '';

        $('#staffModal').on('hidden.bs.modal', function (e) {
            // do something...
            if (to_add) {
                to_add = false;
                accessAdd($('#staffInfoInternshipReleaseId').val(), to_add_data);
            }
        });

        /**
         * 检验教职工信息
         */
        function validStaff() {
            var staffUsername = $('#staffUsername').val();
            var staffNumber = $('#staffNumber').val();
            if (staffUsername.length <= 0 && staffUsername.length <= 0) {
                validErrorDom(validId.staff, errorMsgId.staff, '请至少填写一项教职工信息');
            } else {
                var staff = "";
                var type = -1;
                if (staffUsername.length > 0) {
                    staff = staffUsername;
                    type = 0;
                }

                if (staffNumber.length > 0) {
                    staff = staffNumber;
                    type = 1;
                }

                // 检验教职工信息
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().valid_staff,
                    type: 'post',
                    data: {staff: staff, internshipReleaseId: init_page_param.internshipReleaseId, type: type},
                    success: function (data) {
                        if (data.state) {
                            to_add_data = data.objectResult;
                            to_add = true;
                            $('#staffModal').modal('hide');
                        } else {
                            validErrorDom(validId.staff, errorMsgId.staff, data.msg);
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

        /**
         * 进入添加
         * @param internshipReleaseId
         * @param staffId
         */
        function accessAdd(internshipReleaseId, staffId) {
            $.address.value(getAjaxUrl().add + '?id=' + internshipReleaseId + '&staffId=' + staffId);
        }

        /*
         批量删除
         */
        $('#regulate_dels').click(function () {
            var regulateIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                regulateIds.push($(ids[i]).val());
            }
            regulate_dels(regulateIds);
        });

        /*
         查看页面
         */
        function look(regulateId) {
            $.address.value(getAjaxUrl().look + '?id=' + regulateId);
        }

        /*
         编辑页面
         */
        function edit(regulateId) {
            $.address.value(getAjaxUrl().edit + '?id=' + regulateId);
        }

        /*
         删除
         */
        function regulate_del(regulateId) {
            var msg;
            msg = Messenger().post({
                message: "确定删除监管记录吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(regulateId);
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
        function regulate_dels(regulateIds) {
            var msg;
            msg = Messenger().post({
                message: "确定删除选中的监管记录吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            dels(regulateIds);
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

        function del(regulateId) {
            sendDelAjax(regulateId, '删除');
        }

        function dels(regulateIds) {
            sendDelAjax(regulateIds.join(','), '批量删除');
        }

        /**
         * 删除ajax
         * @param regulateId
         * @param message
         */
        function sendDelAjax(regulateId, message) {
            Messenger().run({
                successMessage: message + '监管记录成功',
                errorMessage: message + '监管记录失败',
                progressMessage: '正在' + message + '监管记录....'
            }, {
                url: web_path + getAjaxUrl().del,
                type: 'post',
                data: {regulateIds: regulateId, staffId: init_page_param.staffId},
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