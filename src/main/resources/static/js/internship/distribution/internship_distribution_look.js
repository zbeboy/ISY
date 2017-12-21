/**
 * Created by lenovo on 2017-01-25.
 */
//# sourceURL=internship_distribution_look.js
require(["jquery", "handlebars", "nav_active", "datatables.responsive", "jquery.address", "messenger"],
    function ($, Handlebars, nav_active) {

        /*
        参数
        */
        var param = {
            studentUsername: '',
            staffUsername: '',
            studentNumber: '',
            staffNumber: '',
            username: '',
            realName: ''
        };

        /*
        web storage key.
       */
        var webStorageKey = {
            STUDENT_USERNAME: 'INTERNSHIP_DISTRIBUTION_LOOK_STUDENT_USERNAME_SEARCH_' + init_page_param.internshipReleaseId,
            STAFF_USERNAME: 'INTERNSHIP_DISTRIBUTION_LOOK_STAFF_USERNAME_SEARCH_' + init_page_param.internshipReleaseId,
            STUDENT_NUMBER: 'INTERNSHIP_DISTRIBUTION_LOOK_STUDENT_NUMBER_SEARCH_' + init_page_param.internshipReleaseId,
            STAFF_NUMBER: 'INTERNSHIP_DISTRIBUTION_LOOK_STAFF_NUMBER_SEARCH_' + init_page_param.internshipReleaseId,
            USERNAME: 'INTERNSHIP_DISTRIBUTION_LOOK_USERNAME_SEARCH_' + init_page_param.internshipReleaseId,
            REAL_NAME: 'INTERNSHIP_DISTRIBUTION_LOOK_REAL_NAME_SEARCH_' + init_page_param.internshipReleaseId
        };

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                datas: '/web/internship/teacher_distribution/distribution/look/data',
                delete_not_apply: '/web/internship/teacher_distribution/distribution/delete_not_apply',
                back: '/web/menu/internship/teacher_distribution'
            };
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().back);

        var serialNumber = 0;// 序号
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
            "aaSorting": [[3, 'asc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().datas,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.internshipReleaseId = init_page_param.internshipReleaseId;
                    serialNumber = 0;
                }
            },
            "columns": [
                {"data": null},
                {"data": "studentRealName"},
                {"data": "studentUsername"},
                {"data": "studentNumber"},
                {"data": "staffRealName"},
                {"data": "staffUsername"},
                {"data": "staffNumber"},
                {"data": "realName"},
                {"data": "username"}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        serialNumber++;
                        return serialNumber;
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
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '<button type="button" id="delete_not_apply" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>删除未申请学生分配</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                studentUsername: '#search_student_username',
                staffUsername: '#search_staff_username',
                studentNumber: '#search_student_number',
                staffNumber: '#search_staff_number',
                username: '#search_username',
                realName: '#search_real_name'
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
            param.studentUsername = $(getParamId().studentUsername).val();
            param.staffUsername = $(getParamId().staffUsername).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.staffNumber = $(getParamId().staffNumber).val();
            param.username = $(getParamId().username).val();
            param.realName = $(getParamId().realName).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.STUDENT_USERNAME, param.studentUsername);
                sessionStorage.setItem(webStorageKey.STAFF_USERNAME, param.staffUsername);
                sessionStorage.setItem(webStorageKey.STUDENT_NUMBER, param.studentNumber);
                sessionStorage.setItem(webStorageKey.STAFF_NUMBER, param.staffNumber);
                sessionStorage.setItem(webStorageKey.USERNAME, param.username);
                sessionStorage.setItem(webStorageKey.REAL_NAME, param.realName);
            }
        }

        /*
        初始化搜索内容
       */
        function initSearchContent() {
            var studentUsername = null;
            var staffUsername = null;
            var studentNumber = null;
            var staffNumber = null;
            var username = null;
            var realName = null;
            if (typeof(Storage) !== "undefined") {
                studentUsername = sessionStorage.getItem(webStorageKey.STUDENT_USERNAME);
                staffUsername = sessionStorage.getItem(webStorageKey.STAFF_USERNAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                staffNumber = sessionStorage.getItem(webStorageKey.STAFF_NUMBER);
                username = sessionStorage.getItem(webStorageKey.USERNAME);
                realName = sessionStorage.getItem(webStorageKey.REAL_NAME);
            }
            if (studentUsername !== null) {
                param.studentUsername = studentUsername;
            }

            if (staffUsername !== null) {
                param.staffUsername = staffUsername;
            }

            if (studentNumber !== null) {
                param.studentNumber = studentNumber;
            }

            if (staffNumber !== null) {
                param.staffNumber = staffNumber;
            }

            if (username !== null) {
                param.username = username;
            }

            if (realName !== null) {
                param.realName = realName;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var studentUsername = null;
            var staffUsername = null;
            var studentNumber = null;
            var staffNumber = null;
            var username = null;
            var realName = null;
            if (typeof(Storage) !== "undefined") {
                studentUsername = sessionStorage.getItem(webStorageKey.STUDENT_USERNAME);
                staffUsername = sessionStorage.getItem(webStorageKey.STAFF_USERNAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                staffNumber = sessionStorage.getItem(webStorageKey.STAFF_NUMBER);
                username = sessionStorage.getItem(webStorageKey.USERNAME);
                realName = sessionStorage.getItem(webStorageKey.REAL_NAME);
            }
            if (studentUsername !== null) {
                $(getParamId().studentUsername).val(studentUsername);
            }

            if (staffUsername !== null) {
                $(getParamId().staffUsername).val(staffUsername);
            }

            if (studentNumber !== null) {
                $(getParamId().studentNumber).val(studentNumber);
            }

            if (staffNumber !== null) {
                $(getParamId().staffNumber).val(staffNumber);
            }

            if (username !== null) {
                $(getParamId().username).val(username);
            }

            if (realName !== null) {
                $(getParamId().realName).val(realName);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentUsername).val('');
            $(getParamId().staffUsername).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().staffNumber).val('');
            $(getParamId().username).val('');
            $(getParamId().realName).val('');
        }

        $(getParamId().studentUsername).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().staffUsername).keyup(function (event) {
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

        $(getParamId().staffNumber).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().username).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().realName).keyup(function (event) {
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
         返回
         */
        $('#page_back').click(function () {
            $.address.value(getAjaxUrl().back);
        });

        /*
         删除未申请学生分配
         */
        $('#delete_not_apply').click(function () {
            var id = init_page_param.internshipReleaseId;
            var msg;
            msg = Messenger().post({
                message: "确定删除未申请学生的分配吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            sendDeleteNotApplyAjax(id);
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
        });

        /**
         * 发送删除未申请学生分配
         */
        function sendDeleteNotApplyAjax(id) {
            $.post(web_path + getAjaxUrl().delete_not_apply, {id: id}, function (data) {
                if (data.state) {
                    myTable.ajax.reload();
                    Messenger().post({
                        message: data.msg,
                        type: 'info',
                        showCloseButton: true
                    });
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