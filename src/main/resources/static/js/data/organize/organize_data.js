/**
 * Created by lenovo on 2016-09-25.
 */
require(["jquery", "handlebars", "lodash_plugin", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, Handlebars, DP) {

        /*
        参数
        */
        var param = {
            schoolName: '',
            collegeName: '',
            departmentName: '',
            scienceName: '',
            organizeName: '',
            grade: ''
        };

        /*
        web storage key.
        */
        var webStorageKey = {
            SCHOOL_NAME: 'DATA_ORGANIZE_SCHOOL_NAME_SEARCH',
            COLLEGE_NAME: 'DATA_ORGANIZE_COLLEGE_NAME_SEARCH',
            DEPARTMENT_NAME: 'DATA_ORGANIZE_DEPARTMENT_NAME_SEARCH',
            SCIENCE_NAME: 'DATA_ORGANIZE_SCIENCE_NAME_SEARCH',
            ORGANIZE_NAME: 'DATA_ORGANIZE_ORGANIZE_NAME_SEARCH',
            GRADE: 'DATA_ORGANIZE_GRADE_SEARCH'
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

        /**
         * 获取分页信息
         * @returns {number}
         */
        function getPage() {
            var page = 0;
            if (myTable) {
                page = myTable.page();
            }
            return page;
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
                "url": web_path + getAjaxUrl().organizes,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.extra_page = getPage();
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

                        return template(context);
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-4'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'));
                });

                tableElement.delegate('.del', "click", function () {
                    organize_del($(this).attr('data-id'), $(this).attr('data-organize'));
                });

                tableElement.delegate('.recovery', "click", function () {
                    organize_recovery($(this).attr('data-id'), $(this).attr('data-organize'));
                });
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '<button type="button" id="organize_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="organize_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量注销</button>' +
            '  <button type="button" id="organize_recoveries" class="btn btn-outline btn-warning btn-sm"><i class="fa fa-reply-all"></i>批量恢复</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
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
            param.scienceName = $(getParamId().scienceName).val();
            param.organizeName = $(getParamId().organizeName).val();
            param.grade = $(getParamId().grade).val();

            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.SCHOOL_NAME, DP.defaultUndefinedValue(param.schoolName, ''));
                sessionStorage.setItem(webStorageKey.COLLEGE_NAME, DP.defaultUndefinedValue(param.collegeName, ''));
                sessionStorage.setItem(webStorageKey.DEPARTMENT_NAME, DP.defaultUndefinedValue(param.departmentName, ''));
                sessionStorage.setItem(webStorageKey.SCIENCE_NAME, param.scienceName);
                sessionStorage.setItem(webStorageKey.ORGANIZE_NAME, param.organizeName);
                sessionStorage.setItem(webStorageKey.GRADE, param.grade);
            }
        }

        /*
        初始化搜索内容
        */
        function initSearchContent() {
            var schoolName = null;
            var collegeName = null;
            var departmentName = null;
            var scienceName = null;
            var organizeName = null;
            var grade = null;
            if (typeof(Storage) !== "undefined") {
                schoolName = sessionStorage.getItem(webStorageKey.SCHOOL_NAME);
                collegeName = sessionStorage.getItem(webStorageKey.COLLEGE_NAME);
                departmentName = sessionStorage.getItem(webStorageKey.DEPARTMENT_NAME);
                scienceName = sessionStorage.getItem(webStorageKey.SCIENCE_NAME);
                organizeName = sessionStorage.getItem(webStorageKey.ORGANIZE_NAME);
                grade = sessionStorage.getItem(webStorageKey.GRADE);
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

            if (scienceName !== null) {
                param.scienceName = scienceName;
            }

            if (organizeName !== null) {
                param.organizeName = organizeName;
            }

            if (grade !== null) {
                param.grade = grade;
            }
        }

        /*
       初始化搜索框
        */
        function initSearchInput() {
            var schoolName = null;
            var collegeName = null;
            var departmentName = null;
            var scienceName = null;
            var organizeName = null;
            var grade = null;
            if (typeof(Storage) !== "undefined") {
                schoolName = sessionStorage.getItem(webStorageKey.SCHOOL_NAME);
                collegeName = sessionStorage.getItem(webStorageKey.COLLEGE_NAME);
                departmentName = sessionStorage.getItem(webStorageKey.DEPARTMENT_NAME);
                scienceName = sessionStorage.getItem(webStorageKey.SCIENCE_NAME);
                organizeName = sessionStorage.getItem(webStorageKey.ORGANIZE_NAME);
                grade = sessionStorage.getItem(webStorageKey.GRADE);
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

            if (scienceName !== null) {
                $(getParamId().scienceName).val(scienceName);
            }

            if (organizeName !== null) {
                $(getParamId().organizeName).val(organizeName);
            }

            if (grade !== null) {
                $(getParamId().grade).val(grade);
            }
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

        $(getParamId().scienceName).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().organizeName).keyup(function (event) {
            if (event.keyCode == 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().grade).keyup(function (event) {
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
        $('#organize_add').click(function () {
            $.address.value(getAjaxUrl().add);
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
            $.address.value(getAjaxUrl().edit + '?id=' + organizeId);
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
         * @param organizeId
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