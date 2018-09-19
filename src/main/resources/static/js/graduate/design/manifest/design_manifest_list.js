/**
 * Created by zbeboy on 2017/8/1.
 */
require(["jquery", "handlebars", "constants", "nav_active", "bootstrap-select-zh-CN", "datatables.responsive", "jquery.address", "messenger"],
    function ($, Handlebars, constants, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/manifest/list/data',
                teachers: '/anyone/graduate/design/teachers',
                export_data_url: '/web/graduate/design/manifest/list/data/export',
                mark_info_url: '/web/graduate/design/manifest/mark/info',
                mark_url: '/web/graduate/design/manifest/mark',
                scores: '/user/scores',
                back: '/web/menu/graduate/design/manifest'
            };
        }

        /*
         参数id
         */
        function getParamId() {
            return {
                staffId: '#select_staff',
                studentName: '#search_student_name',
                studentNumber: '#search_student_number'
            };
        }

        /*
        参数
       */
        var param = {
            staffId: init_page_param.staffId,
            studentName: '',
            studentNumber: ''
        };

        /*
        web storage key.
       */
        var webStorageKey = {
            STUDENT_NAME: 'GRADUATE_DESIGN_MANIFEST_LIST_STUDENT_NAME_SEARCH_' + init_page_param.graduationDesignReleaseId,
            STUDENT_NUMBER: 'GRADUATE_DESIGN_MANIFEST_LIST_STUDENT_NUMBER_SEARCH_' + init_page_param.graduationDesignReleaseId
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
                $('[data-toggle="tooltip"]').tooltip();
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[6, 'asc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                    d.staffId = getParam().staffId;
                }
            },
            "columns": [
                {"data": "presubjectTitle"},
                {"data": "subjectTypeName"},
                {"data": "originTypeName"},
                {"data": "staffName"},
                {"data": "academicTitleName"},
                {"data": "guidePeoples"},
                {"data": "studentNumber"},
                {"data": "studentName"},
                {"data": "scoreTypeName"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
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
                    targets: 9,
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
                                            "name": "成绩",
                                            "css": "grade",
                                            "type": "info",
                                            "id": c.defenseOrderId,
                                            "staff": c.staffId
                                        }
                                    ]
                                };
                        } else {
                            if (init_page_param.usersTypeName === constants.global_users_type.staff_type) {// 教师
                                if (c.staffId === init_page_param.staffId && init_page_param.staffId !== 0) {
                                    context =
                                        {
                                            func: [
                                                {
                                                    "name": "成绩",
                                                    "css": "grade",
                                                    "type": "info",
                                                    "id": c.defenseOrderId,
                                                    "staff": c.staffId
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
                tableElement.delegate('.grade', "click", function () {
                    grade($(this).attr('data-id'), $(this).attr('data-staff'));
                });
                // 初始化搜索框中内容
                initSearchInput();
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
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.STUDENT_NAME, param.studentName);
                sessionStorage.setItem(webStorageKey.STUDENT_NUMBER, param.studentNumber);
            }
        }

        /*
        初始化搜索内容
       */
        function initSearchContent() {
            var studentName = null;
            var studentNumber = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
            }
            if (studentName !== null) {
                param.studentName = studentName;
            }

            if (studentNumber !== null) {
                param.studentNumber = studentNumber;
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
        }

        $(getParamId().staffId).on('changed.bs.select', function (e) {
            initParam();
            myTable.ajax.reload();
        });

        $(getParamId().studentName).keyup(function (event) {
            if (event.keyCode === 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().studentNumber).keyup(function (event) {
            if (event.keyCode === 13) {
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

        pageAop.delegate('#refresh', "click", function () {
            myTable.ajax.reload();
        });

        init();

        function init() {
            initTeachers();
            initScore();
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
         * 初始化成绩数据
         */
        function initScore() {
            $.get(web_path + getAjaxUrl().scores, function (data) {
                if (data.state) {
                    markScoreData(data);
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
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.staffRealName + ' ' + this.staffMobile));
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
         * 修改成绩数据
         * @param data
         */
        function markScoreData(data) {
            var template = Handlebars.compile($("#mark-score-template").html());
            var scoreData = $('#scoreData');
            scoreData.html(template(data));
            $(scoreData.children()[0]).remove();
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
                window.location.href = encodeURI(web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&graduationDesignReleaseId=" + graduationDesignReleaseId + '&staffId=' + getParam().staffId);
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
                window.location.href = encodeURI(web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&graduationDesignReleaseId=" + graduationDesignReleaseId + '&staffId=' + getParam().staffId);
            } else {
                Messenger().post({
                    message: '请选择指导教师',
                    type: 'error',
                    showCloseButton: true
                });
            }

        });

        /**
         * 成绩
         * @param id
         * @param staffId
         */
        function grade(id, staffId) {
            $.post(web_path + getAjaxUrl().mark_info_url, {
                graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
                defenseOrderId: id
            }, function (data) {
                if (data.state) {
                    selectedScore(data.objectResult.scoreTypeId);
                    $('#markDefenseOrderId').val(id);
                    $('#markStaffId').val(staffId);
                    $('#markModalLabel').text(data.objectResult.studentName + ' ' + data.objectResult.studentNumber);
                    $('#markModal').modal('show');
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        function selectedScore(scoreTypeId) {
            var scoreTypes = $('.markScore');
            for (var i = 0; i < scoreTypes.length; i++) {
                if (Number($(scoreTypes[i]).val()) === scoreTypeId) {
                    $(scoreTypes[i]).prop('checked', true);
                    break;
                }
            }
        }

        // 成绩确定
        $('#toMark').click(function () {
            $.post(web_path + getAjaxUrl().mark_url, $('#markForm').serialize(), function (data) {
                if (data.state) {
                    $('#markModal').modal('hide');
                    myTable.ajax.reload();
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        });

    });