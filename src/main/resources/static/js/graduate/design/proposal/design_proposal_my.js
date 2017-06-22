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
            "aaSorting": [[1, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": null},
                {"data": "schoolId"},
                {"data": "schoolName"},
                {"data": "schoolIsDel"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.schoolId + '" name="check"/>';
                    }
                },
                {
                    targets: 4,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context = null;

                        if (c.schoolIsDel == 0 || c.schoolIsDel == null) {
                            context =
                            {
                                func: [
                                    {
                                        "name": "编辑",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.schoolId,
                                        "school": c.schoolName
                                    },
                                    {
                                        "name": "注销",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.schoolId,
                                        "school": c.schoolName
                                    }
                                ]
                            };
                        } else {
                            context =
                            {
                                func: [
                                    {
                                        "name": "编辑",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.schoolId,
                                        "school": c.schoolName
                                    },
                                    {
                                        "name": "恢复",
                                        "css": "recovery",
                                        "type": "warning",
                                        "id": c.schoolId,
                                        "school": c.schoolName
                                    }
                                ]
                            };
                        }

                        return template(context);
                    }
                },
                {
                    targets: 3,
                    render: function (a, b, c, d) {
                        if (c.schoolIsDel == 0 || c.schoolIsDel == null) {
                            return "<span class='text-info'>正常</span>";
                        } else {
                            return "<span class='text-danger'>已注销</span>";
                        }
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

                });
            }
        });

        var global_button = '<button type="button" id="school_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="school_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量注销</button>' +
            '  <button type="button" id="school_recoveries" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-reply-all"></i>批量恢复</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                schoolName: '#search_school'
            };
        }

        /*
         参数
         */
        var param = {
            schoolName: ''
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
            param.schoolName = $(getParamId().schoolName).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().schoolName).val('');
        }

        $(getParamId().schoolName).keyup(function (event) {
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
         添加页面
         */
        $('#school_add').click(function () {
            $.address.value(getAjaxUrl().add);
        });

        /*
         批量注销
         */
        $('#school_dels').click(function () {
            var schoolIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                schoolIds.push($(ids[i]).val());
            }

            if (schoolIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定注销选中的学校吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                dels(schoolIds);
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
                Messenger().post("未发现有选中的学校!");
            }
        });

        /*
         批量恢复
         */
        $('#school_recoveries').click(function () {
            var schoolIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                schoolIds.push($(ids[i]).val());
            }

            if (schoolIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定恢复选中的学校吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                recoveries(schoolIds);
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
                Messenger().post("未发现有选中的学校!");
            }
        });

        /*
         编辑页面
         */
        function edit(schoolId) {
            $.address.value(getAjaxUrl().edit + '?id=' + schoolId);
        }

        /*
         注销
         */
        function school_del(schoolId, schoolName) {
            var msg;
            msg = Messenger().post({
                message: "确定注销学校 '" + schoolName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(schoolId);
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
         恢复
         */
        function school_recovery(schoolId, schoolName) {
            var msg;
            msg = Messenger().post({
                message: "确定恢复学校 '" + schoolName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            recovery(schoolId);
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

        function del(schoolId) {
            sendUpdateDelAjax(schoolId, '注销', 1);
        }

        function recovery(schoolId) {
            sendUpdateDelAjax(schoolId, '恢复', 0);
        }

        function dels(schoolIds) {
            sendUpdateDelAjax(schoolIds.join(","), '批量注销', 1);
        }

        function recoveries(schoolIds) {
            sendUpdateDelAjax(schoolIds.join(","), '批量恢复', 0);
        }

        /**
         * 注销或恢复ajax
         * @param schoolId
         * @param message
         * @param isDel
         */
        function sendUpdateDelAjax(schoolId, message, isDel) {
            Messenger().run({
                successMessage: message + '学校成功',
                errorMessage: message + '学校失败',
                progressMessage: '正在' + message + '学校....'
            }, {
                url: web_path + getAjaxUrl().updateDel,
                type: 'post',
                data: {schoolIds: schoolId, isDel: isDel},
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