/**
 * Created by zbeboy on 2017/6/22.
 */
require(["jquery", "handlebars", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/proposal/my/data'
            };
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
                $('#checkall').prop('checked', false);
                // 调用全选插件
                $.fn.check({checkall_name: "checkall", checkbox_name: "check"});
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[4, 'asc']],// 排序
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
                {"data": null},
                {"data": "originalFileName"},
                {"data": "graduationDesignDatumTypeName"},
                {"data": "version"},
                {"data": "updateTimeStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.graduationDesignDatumId + '" name="check"/>';
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
                                    "name": "编辑",
                                    "css": "edit",
                                    "type": "primary",
                                    "id": c.graduationDesignDatumId,
                                    "fileName": c.originalFileName
                                },
                                {
                                    "name": "删除",
                                    "css": "del",
                                    "type": "danger",
                                    "id": c.graduationDesignDatumId,
                                    "fileName": c.originalFileName
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
                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
                });

                tableElement.delegate('.del', "click", function () {
                    proposal_del($(this).attr('data-id'), $(this).attr('data-original-file-name'));
                });
            }
        });

        var global_button = '<button type="button" id="proposal_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="proposal_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量删除</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                originalFileName: '#search_file',
                graduationDesignDatumTypeName: '#graduation_design_datum_type'
            };
        }

        /*
         参数
         */
        var param = {
            originalFileName: '',
            graduationDesignDatumTypeName: ''
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
            param.originalFileName = $(getParamId().originalFileName).val();
            param.graduationDesignDatumTypeName = $(getParamId().graduationDesignDatumTypeName).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().originalFileName).val('');
            $(getParamId().graduationDesignDatumTypeName).val('');
        }

        $(getParamId().originalFileName).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().graduationDesignDatumTypeName).change(function () {
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
         添加页面
         */
        $('#proposal_add').click(function () {

        });

        /*
         批量删除
         */
        $('#proposal_dels').click(function () {
            var graduationDesignDatumIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                graduationDesignDatumIds.push($(ids[i]).val());
            }

            if (graduationDesignDatumIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定删除选中的文件吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                dels(graduationDesignDatumIds);
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
                Messenger().post("未发现有选中的文件!");
            }
        });


        /*
         编辑页面
         */
        function edit(graduationDesignDatumId) {

        }

        /*
         删除
         */
        function proposal_del(graduationDesignDatumId, originalFileName) {
            var msg;
            msg = Messenger().post({
                message: "确定删除文件 '" + originalFileName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(graduationDesignDatumId);
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

        function del(graduationDesignDatumId) {
            sendUpdateDelAjax(graduationDesignDatumId, '删除', 1);
        }

        function dels(graduationDesignDatumIds) {
            sendUpdateDelAjax(graduationDesignDatumIds.join(","), '批量删除', 1);
        }

        /**
         * 注销或恢复ajax
         * @param graduationDesignDatumId
         * @param message
         */
        function sendUpdateDelAjax(graduationDesignDatumId, message) {
            Messenger().run({
                successMessage: message + '文件成功',
                errorMessage: message + '文件失败',
                progressMessage: '正在' + message + '文件....'
            }, {
                url: web_path + getAjaxUrl().updateDel,
                type: 'post',
                data: {graduationDesignDatumIds: graduationDesignDatumId},
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