/**
 * Created by zbeboy on 2017/5/27.
 */
require(["jquery", "handlebars", "lodash_plugin", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, DP) {

        /*
        参数
        */
        var param = {
            schoolName: '',
            collegeName: '',
            buildingName: ''
        };

        /*
        web storage key.
       */
        var webStorageKey = {
            SCHOOL_NAME: 'DATA_BUILDING_SCHOOL_NAME_SEARCH',
            COLLEGE_NAME: 'DATA_BUILDING_COLLEGE_NAME_SEARCH',
            BUILDING_NAME: 'DATA_BUILDING_BUILDING_NAME_SEARCH'
        };

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                buildings: '/web/data/building/data',
                updateDel: '/web/data/building/update/del',
                add: '/web/data/building/add',
                edit: '/web/data/building/edit'
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
                "url": web_path + getAjaxUrl().buildings,
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
                {"data": "buildingId"},
                {"data": "schoolName"},
                {"data": "collegeName"},
                {"data": "buildingName"},
                {"data": "buildingIsDel"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.buildingId + '" name="check"/>';
                    }
                },
                {
                    targets: 6,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context = null;

                        if (c.buildingIsDel === 0 || c.buildingIsDel == null) {
                            context =
                                {
                                    func: [
                                        {
                                            "name": "编辑",
                                            "css": "edit",
                                            "type": "primary",
                                            "id": c.buildingId,
                                            "building": c.buildingName
                                        },
                                        {
                                            "name": "注销",
                                            "css": "del",
                                            "type": "danger",
                                            "id": c.buildingId,
                                            "building": c.buildingName
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
                                            "id": c.buildingId,
                                            "building": c.buildingName
                                        },
                                        {
                                            "name": "恢复",
                                            "css": "recovery",
                                            "type": "warning",
                                            "id": c.buildingId,
                                            "building": c.buildingName
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
                        if (c.buildingIsDel === 0 || c.buildingIsDel == null) {
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
                    edit($(this).attr('data-id'));
                });

                tableElement.delegate('.del', "click", function () {
                    building_del($(this).attr('data-id'), $(this).attr('data-building'));
                });

                tableElement.delegate('.recovery', "click", function () {
                    building_recovery($(this).attr('data-id'), $(this).attr('data-building'));
                });
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '<button type="button" id="building_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="building_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量注销</button>' +
            '  <button type="button" id="building_recoveries" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-reply-all"></i>批量恢复</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                schoolName: '#search_school',
                collegeName: '#search_college',
                buildingName: '#search_building'
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
            param.buildingName = $(getParamId().buildingName).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.SCHOOL_NAME, DP.defaultUndefinedValue(param.schoolName, ''));
                sessionStorage.setItem(webStorageKey.COLLEGE_NAME, DP.defaultUndefinedValue(param.collegeName, ''));
                sessionStorage.setItem(webStorageKey.BUILDING_NAME, param.buildingName);
            }
        }

        /*
        初始化搜索内容
         */
        function initSearchContent() {
            var schoolName = null;
            var collegeName = null;
            var buildingName = null;
            if (typeof(Storage) !== "undefined") {
                schoolName = sessionStorage.getItem(webStorageKey.SCHOOL_NAME);
                collegeName = sessionStorage.getItem(webStorageKey.COLLEGE_NAME);
                buildingName = sessionStorage.getItem(webStorageKey.BUILDING_NAME);
            }
            if (schoolName !== null) {
                param.schoolName = schoolName;
            }

            if (collegeName !== null) {
                param.collegeName = collegeName;
            }

            if (buildingName !== null) {
                param.buildingName = buildingName;
            }
        }

        /*
       初始化搜索框
        */
        function initSearchInput() {
            var schoolName = null;
            var collegeName = null;
            var buildingName = null;
            if (typeof(Storage) !== "undefined") {
                schoolName = sessionStorage.getItem(webStorageKey.SCHOOL_NAME);
                collegeName = sessionStorage.getItem(webStorageKey.COLLEGE_NAME);
                buildingName = sessionStorage.getItem(webStorageKey.BUILDING_NAME);
            }
            if (schoolName !== null) {
                $(getParamId().schoolName).val(schoolName);
            }

            if (collegeName !== null) {
                $(getParamId().collegeName).val(collegeName);
            }

            if (buildingName !== null) {
                $(getParamId().buildingName).val(buildingName);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().schoolName).val('');
            $(getParamId().collegeName).val('');
            $(getParamId().buildingName).val('');
        }

        $(getParamId().schoolName).keyup(function (event) {
            if (event.keyCode === 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().collegeName).keyup(function (event) {
            if (event.keyCode === 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().buildingName).keyup(function (event) {
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

        $('#refresh').click(function () {
            myTable.ajax.reload();
        });

        /*
         添加页面
         */
        $('#building_add').click(function () {
            $.address.value(getAjaxUrl().add);
        });

        /*
         批量注销
         */
        $('#building_dels').click(function () {
            var buildingIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                buildingIds.push($(ids[i]).val());
            }

            if (buildingIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定注销选中的楼吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                dels(buildingIds);
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
                Messenger().post("未发现有选中的楼!");
            }
        });

        /*
         批量恢复
         */
        $('#building_recoveries').click(function () {
            var buildingIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                buildingIds.push($(ids[i]).val());
            }

            if (buildingIds.length > 0) {
                var msg;
                msg = Messenger().post({
                    message: "确定恢复选中的楼吗?",
                    actions: {
                        retry: {
                            label: '确定',
                            phrase: 'Retrying TIME',
                            action: function () {
                                msg.cancel();
                                recoveries(buildingIds);
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
                Messenger().post("未发现有选中的楼!");
            }
        });

        /*
         编辑页面
         */
        function edit(buildingId) {
            $.address.value(getAjaxUrl().edit + '?id=' + buildingId);
        }

        /*
         注销
         */
        function building_del(buildingId, buildingName) {
            var msg;
            msg = Messenger().post({
                message: "确定注销楼 '" + buildingName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(buildingId);
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
        function building_recovery(buildingId, buildingName) {
            var msg;
            msg = Messenger().post({
                message: "确定恢复楼 '" + buildingName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            recovery(buildingId);
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

        function del(buildingId) {
            sendUpdateDelAjax(buildingId, '注销', 1);
        }

        function recovery(buildingId) {
            sendUpdateDelAjax(buildingId, '恢复', 0);
        }

        function dels(buildingIds) {
            sendUpdateDelAjax(buildingIds.join(","), '批量注销', 1);
        }

        function recoveries(buildingIds) {
            sendUpdateDelAjax(buildingIds.join(","), '批量恢复', 0);
        }

        /**
         * 注销或恢复ajax
         * @param buildingId
         * @param message
         * @param isDel
         */
        function sendUpdateDelAjax(buildingId, message, isDel) {
            Messenger().run({
                successMessage: message + '楼成功',
                errorMessage: message + '楼失败',
                progressMessage: '正在' + message + '楼....'
            }, {
                url: web_path + getAjaxUrl().updateDel,
                type: 'post',
                data: {buildingIds: buildingId, isDel: isDel},
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