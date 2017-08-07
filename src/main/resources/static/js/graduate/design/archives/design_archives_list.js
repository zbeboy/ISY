/**
 * Created by lenovo on 2017-08-05.
 */
require(["jquery", "handlebars", "constants", "nav_active", "datatables.responsive", "jquery.address", "messenger"],
    function ($, Handlebars, constants, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/archives/list/data',
                export_data_url: '/web/graduate/design/archives/list/data/export',
                generateArchivesCode_url: '/web/graduate/design/archives/generate',
                excellent_url: '/web/graduate/design/archives/excellent',
                archive_info: '/web/graduate/design/archives/info',
                valid_archive_number: '/web/graduate/design/archives/valid/number',
                archive_number_update: '/web/graduate/design/archives/number',
                back: '/web/menu/graduate/design/archives'
            };
        }

        /*
         参数id
         */
        function getParamId() {
            return {
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                staffName: '#search_staff_name',
                staffNumber: '#search_staff_number'
            };
        }

        /*
         参数
         */
        var param = {
            studentName: '',
            studentNumber: '',
            staffName: '',
            staffNumber: ''
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
            "aaSorting": [[14, 'asc']],// 排序
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
                {"data": "collegeName"},
                {"data": "collegeCode"},
                {"data": "scienceName"},
                {"data": "scienceCode"},
                {"data": "graduationDate"},
                {"data": "staffName"},
                {"data": "staffNumber"},
                {"data": "academicTitleName"},
                {"data": "assistantTeacher"},
                {"data": "assistantTeacherNumber"},
                {"data": "assistantTeacherAcademic"},
                {"data": "presubjectTitle"},
                {"data": "subjectTypeName"},
                {"data": "originTypeName"},
                {"data": "studentNumber"},
                {"data": "studentName"},
                {"data": "scoreTypeName"},
                {"data": "isExcellent"},
                {"data": "archiveNumber"},
                {"data": "note"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 11,
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
                    targets: 17,
                    render: function (a, b, c, d) {
                        var v = '否';
                        if (c.isExcellent !== null && c.isExcellent === 1) {
                            v = '是';
                        }
                        return v;
                    }
                },
                {
                    targets: 20,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context = null;

                        if (c.isExcellent !== null && c.isExcellent === 1) {
                            context = {
                                func: [
                                    {
                                        "name": "取消百优",
                                        "css": "cancelExcellent",
                                        "type": "primary",
                                        "id": c.graduationDesignPresubjectId
                                    },
                                    {
                                        "name": "档案号",
                                        "css": "archiveNumber",
                                        "type": "default",
                                        "id": c.graduationDesignPresubjectId
                                    },
                                    {
                                        "name": "备注",
                                        "css": "note",
                                        "type": "default",
                                        "id": c.graduationDesignPresubjectId
                                    }
                                ]
                            };
                        } else {
                            context = {
                                func: [
                                    {
                                        "name": "设置百优",
                                        "css": "okExcellent",
                                        "type": "default",
                                        "id": c.graduationDesignPresubjectId
                                    },
                                    {
                                        "name": "档案号",
                                        "css": "archiveNumber",
                                        "type": "default",
                                        "id": c.graduationDesignPresubjectId
                                    },
                                    {
                                        "name": "备注",
                                        "css": "note",
                                        "type": "default",
                                        "id": c.graduationDesignPresubjectId
                                    }
                                ]
                            };
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
                tableElement.delegate('.okExcellent', "click", function () {
                    excellent($(this).attr('data-id'), 1, '设置为百篇优秀论文');
                });

                tableElement.delegate('.cancelExcellent', "click", function () {
                    excellent($(this).attr('data-id'), 0, '取消百篇优秀论文');
                });

                tableElement.delegate('.archiveNumber', "click", function () {
                    archive($(this).attr('data-id'));
                });
            }
        });

        var global_button = '<button type="button" id="generate_code" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-archive"></i>生成档案号</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);


        /*
         初始化参数
         */
        function initParam() {
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.staffName = $(getParamId().staffName).val();
            param.staffNumber = $(getParamId().staffNumber).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().staffName).val('');
            $(getParamId().staffNumber).val('');
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

        $(getParamId().staffName).keyup(function (event) {
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

        $('#export_xls').click(function () {
            initParam();
            var searchParam = JSON.stringify(getParam());
            var exportFile = {
                fileName: $('#export_file_name').val(),
                ext: 'xls'
            };
            var graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
            window.location.href = web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&graduationDesignReleaseId=" + graduationDesignReleaseId;
        });

        $('#export_xlsx').click(function () {
            initParam();
            var searchParam = JSON.stringify(getParam());
            var exportFile = {
                fileName: $('#export_file_name').val(),
                ext: 'xlsx'
            };
            var graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
            window.location.href = web_path + getAjaxUrl().export_data_url + "?extra_search=" + searchParam + "&exportFile=" + JSON.stringify(exportFile) + "&graduationDesignReleaseId=" + graduationDesignReleaseId;
        });

        // 生成档案号
        pageAop.delegate('#generate_code', "click", function () {
            $('#generateArchivesModal').modal('show');
        });

        // 改变系部代码
        $('#generateArchivesCode').change(function () {
            var code = $(this).val();
            var generateArchivesAffix = $('#generateArchivesAffix');
            var grade = init_page_param.grade;
            var curYear = init_page_param.curYear;
            var upYear = init_page_param.upYear;
            if (code !== '') {
                generateArchivesAffix.val('BYSJ' + upYear + '-' + curYear + '-BS' + grade + '-' + code + '-');
            } else {
                generateArchivesAffix.val('');
            }
        });

        // 确定生成档案号
        $('#generateArchives').click(function () {
            validGenerateArchivesAffix();
        });

        function validGenerateArchivesAffix() {
            var generateArchivesAffix = $('#generateArchivesAffix').val();
            if (generateArchivesAffix === '' || generateArchivesAffix.length <= 0) {
                Messenger().post({
                    message: '请生成档案号前缀',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGenerateArchivesStart();
            }
        }

        function validGenerateArchivesStart() {
            var generateArchivesStart = $('#generateArchivesStart').val();
            if (generateArchivesStart === '' || generateArchivesStart.length <= 0) {
                Messenger().post({
                    message: '请填写开始序号',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                sendGenerateArchivesCodeAjax();
            }
        }

        /**
         * 发送生成档案号ajax
         */
        function sendGenerateArchivesCodeAjax() {
            $.post(web_path + getAjaxUrl().generateArchivesCode_url, $('#generateArchivesForm').serialize(), function (data) {
                if (data.state) {
                    $('#generateArchivesModal').modal('hide');
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

        /**
         * 百优
         * @param graduationDesignPresubjectId
         * @param isExcellent
         * @param message
         */
        function excellent(graduationDesignPresubjectId, isExcellent, message) {
            var msg;
            msg = Messenger().post({
                message: "确定" + message + "吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            setExcellent(graduationDesignPresubjectId, isExcellent);
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

        /**
         * 设置百优ajax
         * @param graduationDesignPresubjectId
         * @param isExcellent
         */
        function setExcellent(graduationDesignPresubjectId, isExcellent) {
            $.post(web_path + getAjaxUrl().excellent_url, {
                id: init_page_param.graduationDesignReleaseId,
                graduationDesignPresubjectId: graduationDesignPresubjectId,
                excellent: isExcellent
            }, function (data) {
                if (data.state) {
                    myTable.ajax.reload();
                    Messenger().post({
                        message: data.msg,
                        type: 'success',
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

        // 全局档案号
        var global_archive_number = '';
        var global_has_archives = false;

        /**
         * 设置单个档案号
         * @param graduationDesignPresubjectId
         */
        function archive(graduationDesignPresubjectId) {
            $.post(web_path + getAjaxUrl().archive_info, {graduationDesignPresubjectId: graduationDesignPresubjectId}, function (data) {
                if (data.state) {
                    $('#archiveNumber').val(data.objectResult.archiveNumber);
                    $('#archivesGraduationDesignPresubjectId').val(graduationDesignPresubjectId);
                    $('#archivesModal').modal('show');
                    global_archive_number = data.objectResult.archiveNumber;
                    global_has_archives = true;
                } else {
                    $('#archivesGraduationDesignPresubjectId').val(graduationDesignPresubjectId);
                    $('#archivesModal').modal('show');
                    global_has_archives = false;
                }
            });
        }

        // 确定档案号
        $('#archives').click(function () {
            validArchives();
        });

        function validArchives() {
            var archiveNumber = $('#archiveNumber').val();
            if (archiveNumber === '' || archiveNumber.length <= 0) {
                Messenger().post({
                    message: '请填写档案号',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                if (global_has_archives && archiveNumber === global_archive_number) {
                    Messenger().post({
                        message: '档案号未变更',
                        type: 'error',
                        showCloseButton: true
                    });
                } else {
                    $.post(web_path + getAjaxUrl().valid_archive_number, {archiveNumber: archiveNumber}, function (data) {
                        if (data.state) {
                            sendArchivesAjax();
                        } else {
                            Messenger().post({
                                message: data.msg,
                                type: 'error',
                                showCloseButton: true
                            });
                        }
                    });
                }
            }
        }

        /**
         * 发送档案号更改ajax
         */
        function sendArchivesAjax() {
            $.post(web_path + getAjaxUrl().archive_number_update, $('#archivesForm').serialize(), function (data) {
                if (data.state) {
                    $('#archivesModal').modal('hide');
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