/**
 * Created by lenovo on 2016/12/14.
 */
//# sourceURL=internship_regulate_my.js
require(["jquery", "handlebars", "nav_active", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, nav_active) {

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
                studentNumber: '#search_student_number'
            };
        }

        /*
         参数
         */
        var param = {
            studentName: '',
            studentNumber: ''
        };

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
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.internshipReleaseId = init_page_param.internshipReleaseId;
                    d.staffId = init_page_param.staffId;
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

                        var context =
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
            }
        });

        var global_button = '<button type="button" id="regulate_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="regulate_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量删除</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         初始化参数
         */
        function initParam() {
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
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
            $.address.value(getAjaxUrl().add + '?id=' + init_page_param.internshipReleaseId + '&staffId=' + init_page_param.staffId);
        });

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