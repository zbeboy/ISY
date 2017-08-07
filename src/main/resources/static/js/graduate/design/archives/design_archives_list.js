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
            "aaSorting": [[6, 'asc']],// 排序
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
                    targets: 20,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context =
                            {
                                func: [
                                    {
                                        "name": "百优",
                                        "css": "excellent",
                                        "type": "default",
                                        "id": c.defenseOrderId
                                    },
                                    {
                                        "name": "档案号",
                                        "css": "archiveNumber",
                                        "type": "default",
                                        "id": c.defenseOrderId
                                    },
                                    {
                                        "name": "备注",
                                        "css": "note",
                                        "type": "default",
                                        "id": c.defenseOrderId
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-5'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.grade', "click", function () {

                });
            }
        });

        var global_button = '<button type="button" id="school_add" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-archive"></i>生成档案号</button>' +
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

        init();

        function init() {

        }

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

    });