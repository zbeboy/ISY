/**
 * Created by lenovo on 2016-09-22.
 */
require(["jquery", "handlebars", "lodash_plugin", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, DP) {

        /*
         参数
        */
        var param = {
            schoolName: '',
            collegeName: '',
            departmentName: ''
        };

        /*
        web storage key.
        */
        var webStorageKey = {
            SCHOOL_NAME: 'DATA_DEPARTMENT_SCHOOL_NAME_SEARCH',
            COLLEGE_NAME: 'DATA_DEPARTMENT_COLLEGE_NAME_SEARCH',
            DEPARTMENT_NAME: 'DATA_DEPARTMENT_DEPARTMENT_NAME_SEARCH'
        };

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                departments: '/web/data/department/data',
                updateDel: '/web/data/department/update/del',
                add: '/web/data/department/add',
                edit: '/web/data/department/edit'
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
            "aaSorting": [[1, 'asc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().departments,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": null},
                {"data": "departmentId"},
                {"data": "schoolName"},
                {"data": "collegeName"},
                {"data": "departmentName"},
                {"data": "departmentIsDel"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.departmentId + '" name="check"/>';
                    }
                },
                {
                    targets: 6,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context = null;

                        if (c.departmentIsDel == 0 || c.departmentIsDel == null) {
                            context =
                                {
                                    func: [
                                        {
                                            "name": "编辑",
                                            "css": "edit",
                                            "type": "primary",
                                            "id": c.departmentId,
                                            "department": c.departmentName
                                        },
                                        {
                                            "name": "注销",
                                            "css": "del",
                                            "type": "danger",
                                            "id": c.departmentId,
                                            "department": c.departmentName
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
                                            "id": c.departmentId,
                                            "department": c.departmentName
                                        },
                                        {
                                            "name": "恢复",
                                            "css": "recovery",
                                            "type": "warning",
                                            "id": c.departmentId,
                                            "department": c.departmentName
                                        }
                                    ]
                                };
                        }

                        return template(context);
                    }
                },
                {
                    targets: 5,
                    render: function (a, b, c, d) {
                        if (c.departmentIsDel == 0 || c.departmentIsDel == null) {
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-5'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
                });

                tableElement.delegate('.del', "click", function () {
                    department_del($(this).attr('data-id'), $(this).attr('data-department'));
                });

                tableElement.delegate('.recovery', "click", function () {
                    department_recovery($(this).attr('data-id'), $(this).attr('data-department'));
                });

                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '<button type="button" id="department_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="department_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量注销</button>' +
            '  <button type="button" id="department_recoveries" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-reply-all"></i>批量恢复</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                schoolName: '#search_school',
                collegeName: '#search_college',
                departmentName: '#search_department'
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
            param.schoolName = $(getParamId().schoolName).val();
            param.collegeName = $(getParamId().collegeName).val();
            param.departmentName = $(getParamId().departmentName).val();

            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.SCHOOL_NAME, DP.defaultUndefinedValue(param.schoolName));
                sessionStorage.setItem(webStorageKey.COLLEGE_NAME, DP.defaultUndefinedValue(param.collegeName));
                sessionStorage.setItem(webStorageKey.DEPARTMENT_NAME, param.departmentName);
            }
        }

        /*
        初始化搜索内容
        */
        function initSearchContent() {
            var schoolName = null;
            var collegeName = null;
            var departmentName = null;
            if (typeof(Storage) !== "undefined") {
                schoolName = sessionStorage.getItem(webStorageKey.SCHOOL_NAME);
                collegeName = sessionStorage.getItem(webStorageKey.COLLEGE_NAME);
                departmentName = sessionStorage.getItem(webStorageKey.DEPARTMENT_NAME);
            }
            if (schoolName !== null) {
                param.schoolName = schoolName;
            }

            if (collegeName !== null) {
                param.collegeName = collegeName;
            }

            if (departmentName !== null) {
                param.departmentName = departmentName;
            }
        }

        /*
       初始化搜索框
        */
        function initSearchInput() {
            var schoolName = null;
            var collegeName = null;
            var departmentName = null;
            if (typeof(Storage) !== "undefined") {
                schoolName = sessionStorage.getItem(webStorageKey.SCHOOL_NAME);
                collegeName = sessionStorage.getItem(webStorageKey.COLLEGE_NAME);
                departmentName = sessionStorage.getItem(webStorageKey.DEPARTMENT_NAME);
            }
            if (schoolName !== null) {
                $(getParamId().schoolName).val(schoolName);
            }

            if (collegeName !== null) {
                $(getParamId().collegeName).val(collegeName);
            }

            if (departmentName !== null) {
                $(getParamId().departmentName).val(departmentName);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().schoolName).val('');
            $(getParamId().collegeName).val('');
            $(getParamId().departmentName).val('');
        }

        $(getParamId().schoolName).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().collegeName).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().departmentName).keyup(function (event) {
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
        $('#department_add').click(function () {
            $.address.value(getAjaxUrl().add);
        });

        /*
         批量注销
         */
        $('#department_dels').click(function () {
            var departmentIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                departmentIds.push($(ids[i]).val());
            }

            if (departmentIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定注销选中的系吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                dels(departmentIds);
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
                Messenger().post("未发现有选中的系!");
            }
        });

        /*
         批量恢复
         */
        $('#department_recoveries').click(function () {
            var departmentIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                departmentIds.push($(ids[i]).val());
            }

            if (departmentIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定恢复选中的系吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                recoveries(departmentIds);
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
                Messenger().post("未发现有选中的系!");
            }
        });

        /*
         编辑页面
         */
        function edit(departmentId) {
            $.address.value(getAjaxUrl().edit + '?id=' + departmentId);
        }

        /*
         注销
         */
        function department_del(departmentId, departmentName) {
            var msg;
            msg = Messenger().post({
                message: "确定注销系 '" + departmentName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(departmentId);
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
        function department_recovery(departmentId, departmentName) {
            var msg;
            msg = Messenger().post({
                message: "确定恢复系 '" + departmentName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            recovery(departmentId);
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

        function del(departmentId) {
            sendUpdateDelAjax(departmentId, '注销', 1);
        }

        function recovery(departmentId) {
            sendUpdateDelAjax(departmentId, '恢复', 0);
        }

        function dels(departmentIds) {
            sendUpdateDelAjax(departmentIds.join(","), '批量注销', 1);
        }

        function recoveries(departmentIds) {
            sendUpdateDelAjax(departmentIds.join(","), '批量恢复', 0);
        }

        /**
         * 注销或恢复ajax
         * @param departmentId
         * @param message
         * @param isDel
         */
        function sendUpdateDelAjax(departmentId, message, isDel) {
            Messenger().run({
                successMessage: message + '系成功',
                errorMessage: message + '系失败',
                progressMessage: '正在' + message + '系....'
            }, {
                url: web_path + getAjaxUrl().updateDel,
                type: 'post',
                data: {departmentIds: departmentId, isDel: isDel},
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