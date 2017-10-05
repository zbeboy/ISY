/**
 * Created by lenovo on 2016-12-11.
 */
require(["jquery", "handlebars", "nav_active", "datatables.responsive", "jquery.address", "messenger"],
    function ($, Handlebars, nav_active) {

        /*
         参数
         */
        var param = {
            studentName: '',
            studentNumber: '',
            collegeClass: '',
            phoneNumber: '',
            headmaster: '',
            schoolGuidanceTeacher: ''
        };

        /*
        web storage key.
        */
        var webStorageKey = {
            STUDENT_NAME: 'INTERNSHIP_STATISTICS_COLLEGE_DATA_STUDENT_NAME_SEARCH_' + init_page_param.internshipReleaseId,
            STUDENT_NUMBER: 'INTERNSHIP_STATISTICS_COLLEGE_DATA_STUDENT_NUMBER_SEARCH_' + init_page_param.internshipReleaseId,
            COLLEGE_CLASS: 'INTERNSHIP_STATISTICS_COLLEGE_DATA_COLLEGE_CLASS_SEARCH_' + init_page_param.internshipReleaseId,
            PHONE_NUMBER: 'INTERNSHIP_STATISTICS_COLLEGE_DATA_PHONE_NUMBER_SEARCH_' + init_page_param.internshipReleaseId,
            HEADMASTER: 'INTERNSHIP_STATISTICS_COLLEGE_DATA_HEADMASTER_SEARCH_' + init_page_param.internshipReleaseId,
            SCHOOL_GUIDANCE_TEACHER: 'INTERNSHIP_STATISTICS_COLLEGE_DATA_SCHOOL_GUIDANCE_TEACHER_SEARCH_' + init_page_param.internshipReleaseId
        };

        // 刷新时选中菜单
        nav_active(getAjaxUrl().back);

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                graduation_practice_college_data_url: '/web/internship/statistical/graduation_practice_college/data',
                export_data_url: '/web/internship/statistical/graduation_practice_college/data/export',
                back: '/web/menu/internship/statistical'
            };
        }

        function byteToBoolean(b) {
            var msg = '';
            if (b == 1) {
                msg = '已交';
            } else if (b <= 0) {
                msg = '未交';
            }
            return msg;
        }

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
            "ajax": {
                "url": web_path + getAjaxUrl().graduation_practice_college_data_url,
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
                {"data": "studentName"},
                {"data": "collegeClass"},
                {"data": "studentSex"},
                {"data": "studentNumber"},
                {"data": "phoneNumber"},
                {"data": "qqMailbox"},
                {"data": "parentalContact"},
                {"data": "headmaster"},
                {"data": "headmasterContact"},
                {"data": "graduationPracticeCollegeName"},
                {"data": "graduationPracticeCollegeAddress"},
                {"data": "graduationPracticeCollegeContacts"},
                {"data": "graduationPracticeCollegeTel"},
                {"data": "schoolGuidanceTeacher"},
                {"data": "schoolGuidanceTeacherTel"},
                {"data": "startTime"},
                {"data": "endTime"},
                {"data": "commitmentBook"},
                {"data": "safetyResponsibilityBook"},
                {"data": "practiceAgreement"},
                {"data": "internshipApplication"},
                {"data": "practiceReceiving"},
                {"data": "securityEducationAgreement"},
                {"data": "parentalConsent"}
            ],
            columnDefs: [
                {
                    targets: 17,
                    render: function (a, b, c, d) {
                        return byteToBoolean(c.commitmentBook);
                    }
                },
                {
                    targets: 18,
                    render: function (a, b, c, d) {
                        return byteToBoolean(c.safetyResponsibilityBook);
                    }
                },
                {
                    targets: 19,
                    render: function (a, b, c, d) {
                        return byteToBoolean(c.practiceAgreement);
                    }
                },
                {
                    targets: 20,
                    render: function (a, b, c, d) {
                        return byteToBoolean(c.internshipApplication);
                    }
                },
                {
                    targets: 21,
                    render: function (a, b, c, d) {
                        return byteToBoolean(c.practiceReceiving);
                    }
                },
                {
                    targets: 22,
                    render: function (a, b, c, d) {
                        return byteToBoolean(c.securityEducationAgreement);
                    }
                },
                {
                    targets: 23,
                    render: function (a, b, c, d) {
                        return byteToBoolean(c.parentalConsent);
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

        var global_button = '<button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                collegeClass: '#search_college_class',
                phoneNumber: '#search_phone_number',
                headmaster: '#search_headmaster',
                schoolGuidanceTeacher: '#search_school_guidance_teacher'
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
            param.collegeClass = $(getParamId().collegeClass).val();
            param.phoneNumber = $(getParamId().phoneNumber).val();
            param.headmaster = $(getParamId().headmaster).val();
            param.schoolGuidanceTeacher = $(getParamId().schoolGuidanceTeacher).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.STUDENT_NAME, param.studentName);
                sessionStorage.setItem(webStorageKey.STUDENT_NUMBER, param.studentNumber);
                sessionStorage.setItem(webStorageKey.COLLEGE_CLASS, param.collegeClass);
                sessionStorage.setItem(webStorageKey.PHONE_NUMBER, param.phoneNumber);
                sessionStorage.setItem(webStorageKey.HEADMASTER, param.headmaster);
                sessionStorage.setItem(webStorageKey.SCHOOL_GUIDANCE_TEACHER, param.schoolGuidanceTeacher);
            }
        }

        /*
        初始化搜索内容
       */
        function initSearchContent() {
            var studentName = null;
            var studentNumber = null;
            var collegeClass = null;
            var phoneNumber = null;
            var headmaster = null;
            var schoolGuidanceTeacher = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                collegeClass = sessionStorage.getItem(webStorageKey.COLLEGE_CLASS);
                phoneNumber = sessionStorage.getItem(webStorageKey.PHONE_NUMBER);
                headmaster = sessionStorage.getItem(webStorageKey.HEADMASTER);
                schoolGuidanceTeacher = sessionStorage.getItem(webStorageKey.SCHOOL_GUIDANCE_TEACHER);
            }
            if (studentName !== null) {
                param.studentName = studentName;
            }

            if (studentNumber !== null) {
                param.studentNumber = studentNumber;
            }

            if (collegeClass !== null) {
                param.collegeClass = collegeClass;
            }

            if (phoneNumber !== null) {
                param.phoneNumber = phoneNumber;
            }

            if (headmaster !== null) {
                param.headmaster = headmaster;
            }

            if (schoolGuidanceTeacher !== null) {
                param.schoolGuidanceTeacher = schoolGuidanceTeacher;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var studentName = null;
            var studentNumber = null;
            var collegeClass = null;
            var phoneNumber = null;
            var headmaster = null;
            var schoolGuidanceTeacher = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                collegeClass = sessionStorage.getItem(webStorageKey.COLLEGE_CLASS);
                phoneNumber = sessionStorage.getItem(webStorageKey.PHONE_NUMBER);
                headmaster = sessionStorage.getItem(webStorageKey.HEADMASTER);
                schoolGuidanceTeacher = sessionStorage.getItem(webStorageKey.SCHOOL_GUIDANCE_TEACHER);
            }
            if (studentName !== null) {
                $(getParamId().studentName).val(studentName);
            }

            if (studentNumber !== null) {
                $(getParamId().studentNumber).val(studentNumber);
            }

            if (collegeClass !== null) {
                $(getParamId().collegeClass).val(collegeClass);
            }

            if (phoneNumber !== null) {
                $(getParamId().phoneNumber).val(phoneNumber);
            }

            if (headmaster !== null) {
                $(getParamId().headmaster).val(headmaster);
            }

            if (schoolGuidanceTeacher !== null) {
                $(getParamId().schoolGuidanceTeacher).val(schoolGuidanceTeacher);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().collegeClass).val('');
            $(getParamId().phoneNumber).val('');
            $(getParamId().headmaster).val('');
            $(getParamId().schoolGuidanceTeacher).val('');
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


        $(getParamId().collegeClass).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().phoneNumber).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().headmaster).keyup(function (event) {
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
         返回
         */
        $('#page_back').click(function () {
            $.address.value(getAjaxUrl().back);
        });
    });