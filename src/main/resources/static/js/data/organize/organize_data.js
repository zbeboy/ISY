/**
 * Created by lenovo on 2016-09-25.
 */
requirejs.config({
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "check.all": web_path + "/plugin/checkall/checkall",
        "datatables.responsive": web_path + "/plugin/datatables/js/datatables.responsive",
        "datatables.net": web_path + "/plugin/datatables/js/jquery.dataTables.min",
        "datatables.bootstrap": web_path + "/plugin/datatables/js/dataTables.bootstrap.min",
        "csrf": web_path + "/js/util/csrf",
        "nav": web_path + "/js/util/nav"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "check.all": {
            // jQueryに依存するのでpathsで設定した"module/name"を指定します。
            deps: ["jquery"]
        },
        "datatables.responsive": {
            // jQueryに依存するのでpathsで設定した"module/name"を指定します。
            deps: ["datatables.bootstrap"]
        },
        "messenger": {
            deps: ["jquery"]
        }
    }
});

// require(["module/name", ...], function(params){ ... });
require(["jquery", "requirejs-domready", "messenger", "handlebars", "datatables.responsive", "csrf", "check.all", "nav"], function ($, domready, messenger, Handlebars, dt, csrf, checkall, nav) {
    domready(function () {
        //This function is called once the DOM is ready.
        //It will be safe to query the DOM and manipulate
        //DOM nodes in this function.

        /*
         初始化消息机制
         */
        Messenger.options = {
            extraClasses: 'messenger-fixed messenger-on-bottom messenger-on-right',
            theme: 'flat'
        };

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                organizes: '/web/data/organize/data',
                updateDel: '/web/data/organize/update/del',
                add: '/web/data/organize/add',
                edit: '/web/data/organize/edit'
            };
        }

        var operator_button = $("#operator_button").html();
        // 预编译模板
        var template = Handlebars.compile(operator_button);

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
            "ajax": {
                "url": web_path + getAjaxUrl().organizes,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = initParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": null},
                {"data": "organizeId"},
                {"data": "schoolName"},
                {"data": "collegeName"},
                {"data": "departmentName"},
                {"data": "scienceName"},
                {"data": "grade"},
                {"data": "organizeName"},
                {"data": "organizeIsDel"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.organizeId + '" name="check"/>';
                    }
                },
                {
                    targets: 9,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context = null;

                        if (c.organizeIsDel == 0 || c.organizeIsDel == null) {
                            context =
                            {
                                func: [
                                    {
                                        "name": "编辑",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.organizeId,
                                        "organize": c.organizeName
                                    },
                                    {
                                        "name": "注销",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.organizeId,
                                        "organize": c.organizeName
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
                                        "id": c.organizeId,
                                        "organize": c.organizeName
                                    },
                                    {
                                        "name": "恢复",
                                        "css": "recovery",
                                        "type": "warning",
                                        "id": c.organizeId,
                                        "organize": c.organizeName
                                    }
                                ]
                            };
                        }

                        var html = template(context);
                        return html;
                    }
                },
                {
                    targets: 8,
                    render: function (a, b, c, d) {
                        if (c.organizeIsDel == 0 || c.organizeIsDel == null) {
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-3'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                $(document).on("click", ".edit", function () {
                    edit($(this).attr('data-id'));
                });

                $(document).on("click", ".del", function () {
                    organize_del($(this).attr('data-id'), $(this).attr('data-organize'));
                });

                $(document).on("click", ".recovery", function () {
                    organize_recovery($(this).attr('data-id'), $(this).attr('data-organize'));
                });
            }
        });

        var global_button = '<button type="button" id="organize_add" class="btn btn-outline btn-primary btn-sm">添加</button>' +
            '  <button type="button" id="organize_dels" class="btn btn-outline btn-danger btn-sm">批量注销</button>' +
            '  <button type="button" id="organize_recoveries" class="btn btn-outline btn-warning btn-sm">批量恢复</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                schoolName: '#search_school',
                collegeName: '#search_college',
                departmentName: '#search_department',
                scienceName: '#search_science',
                organizeName: '#search_organize',
                grade: '#search_grade'
            };
        }

        /*
         初始化参数
         */
        function initParam() {
            return {
                schoolName: $(getParamId().schoolName).val(),
                collegeName: $(getParamId().collegeName).val(),
                departmentName: $(getParamId().departmentName).val(),
                scienceName: $(getParamId().scienceName).val(),
                organizeName: $(getParamId().organizeName).val(),
                grade: $(getParamId().grade).val()
            };
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().schoolName).val('');
            $(getParamId().collegeName).val('');
            $(getParamId().departmentName).val('');
            $(getParamId().scienceName).val('');
            $(getParamId().organizeName).val('');
            $(getParamId().grade).val('');
        }

        $(getParamId().schoolName).keyup(function (event) {
            if (event.keyCode == 13) {
                myTable.ajax.reload();
            }
        });

        $(getParamId().collegeName).keyup(function (event) {
            if (event.keyCode == 13) {
                myTable.ajax.reload();
            }
        });

        $(getParamId().departmentName).keyup(function (event) {
            if (event.keyCode == 13) {
                myTable.ajax.reload();
            }
        });

        $(getParamId().scienceName).keyup(function (event) {
            if (event.keyCode == 13) {
                myTable.ajax.reload();
            }
        });

        $(getParamId().organizeName).keyup(function (event) {
            if (event.keyCode == 13) {
                myTable.ajax.reload();
            }
        });

        $(getParamId().grade).keyup(function (event) {
            if (event.keyCode == 13) {
                myTable.ajax.reload();
            }
        });

        $('#search').click(function () {
            myTable.ajax.reload();
        });

        $('#reset_search').click(function () {
            cleanParam();
            myTable.ajax.reload();
        });

        /*
         添加页面
         */
        $('#organize_add').click(function () {
            window.location.href = web_path + getAjaxUrl().add;
        });

        /*
         批量注销
         */
        $('#organize_dels').click(function () {
            var organizeIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                organizeIds.push($(ids[i]).val());
            }

            if (organizeIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定注销选中的班级吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                dels(organizeIds);
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
                Messenger().post("未发现有选中的班级!");
            }
        });

        /*
         批量恢复
         */
        $('#organize_recoveries').click(function () {
            var organizeIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                organizeIds.push($(ids[i]).val());
            }

            if (organizeIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定恢复选中的班级吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                recoveries(organizeIds);
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
                Messenger().post("未发现有选中的班级!");
            }
        });

        /*
         编辑页面
         */
        function edit(organizeId) {
            window.location.href = web_path + getAjaxUrl().edit + '?id=' + organizeId;
        }

        /*
         注销
         */
        function organize_del(organizeId, organizeName) {
            var msg;
            msg = Messenger().post({
                message: "确定注销班级 '" + organizeName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(organizeId);
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
        function organize_recovery(organizeId, organizeName) {
            var msg;
            msg = Messenger().post({
                message: "确定恢复班级 '" + organizeName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            recovery(organizeId);
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

        function del(organizeId) {
            sendUpdateDelAjax(organizeId, '注销', 1);
        }

        function recovery(organizeId) {
            sendUpdateDelAjax(organizeId, '恢复', 0);
        }

        function dels(organizeIds) {
            sendUpdateDelAjax(organizeIds.join(","), '批量注销', 1);
        }

        function recoveries(organizeIds) {
            sendUpdateDelAjax(organizeIds.join(","), '批量恢复', 0);
        }

        /**
         * 注销或恢复ajax
         * @param scienceId
         * @param message
         * @param isDel
         */
        function sendUpdateDelAjax(organizeId, message, isDel) {
            Messenger().run({
                successMessage: message + '班级成功',
                errorMessage: message + '班级失败',
                progressMessage: '正在' + message + '班级....'
            }, {
                url: web_path + getAjaxUrl().updateDel,
                type: 'post',
                data: {organizeIds: organizeId, isDel: isDel},
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
});