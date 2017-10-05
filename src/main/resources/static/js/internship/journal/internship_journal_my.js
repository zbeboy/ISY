/**
 * Created by lenovo on 2016/12/14.
 */
//# sourceURL=internship_journal_my.js
require(["jquery", "handlebars", "nav_active", "moment", "datatables.responsive", "check.all", "jquery.address", "messenger", "bootstrap-daterangepicker"],
    function ($, Handlebars, nav_active, moment) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/internship/journal/list/data',
                del: '/web/internship/journal/list/del',
                edit: '/web/internship/journal/list/edit',
                add: '/web/internship/journal/list/add',
                look: '/web/internship/journal/list/look',
                download: '/web/internship/journal/list/download',
                downloads: '/web/internship/journal/list/my/downloads',
                back: '/web/menu/internship/journal'
            };
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().back);

        /*
         参数id
         */
        function getParamId() {
            return {
                createDate: '#search_create_date'
            };
        }

        /*
         参数
         */
        var param = {
            createDate: ''
        };

        /*
        web storage key.
        */
        var webStorageKey = {
            CREATE_DATE: 'INTERNSHIP_JOURNAL_MY_CREATE_DATE_SEARCH'
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
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.internshipReleaseId = init_page_param.internshipReleaseId;
                    d.studentId = init_page_param.studentId;
                }
            },
            "columns": [
                {"data": null},
                {"data": "studentName"},
                {"data": "studentNumber"},
                {"data": "organize"},
                {"data": "schoolGuidanceTeacher"},
                {"data": "createDateStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    orderable: false,
                    render: function (a, b, c, d) {
                        return '<input type="checkbox" value="' + c.internshipJournalId + '" name="check"/>';
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
                                        "id": c.internshipJournalId
                                    },
                                    {
                                        "name": "编辑",
                                        "css": "edit",
                                        "type": "primary",
                                        "id": c.internshipJournalId
                                    },
                                    {
                                        "name": "删除",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.internshipJournalId
                                    },
                                    {
                                        "name": "下载",
                                        "css": "download",
                                        "type": "default",
                                        "id": c.internshipJournalId
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
                    journal_del($(this).attr('data-id'));
                });

                tableElement.delegate('.download', "click", function () {
                    download($(this).attr('data-id'));
                });
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '<button type="button" id="journal_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="journal_dels" class="btn btn-outline btn-danger btn-sm"><i class="fa fa-trash-o"></i>批量删除</button>' +
            '  <button type="button" id="journal_download_all" class="btn btn-outline btn-default btn-sm"><i class="fa fa-download"></i>下载全部</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         初始化参数
         */
        function initParam() {
            param.createDate = $(getParamId().createDate).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.CREATE_DATE, param.createDate);
            }
        }

        // 创建日期
        $(getParamId().createDate).daterangepicker({
            "startDate": moment().subtract(2, "days"),
            "endDate": moment(),
            "timePicker": true,
            "timePicker24Hour": true,
            "timePickerIncrement": 30,
            "locale": {
                format: 'YYYY-MM-DD HH:mm:ss',
                applyLabel: '确定',
                cancelLabel: '取消',
                fromLabel: '起始时间',
                toLabel: '结束时间',
                customRangeLabel: '自定义',
                separator: ' 至 ',
                daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
                monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                    '七月', '八月', '九月', '十月', '十一月', '十二月']
            }
        }, function (start, end, label) {
            console.log('New date range selected: ' + start.format('YYYY-MM-DD HH:mm:ss') + ' to ' + end.format('YYYY-MM-DD HH:mm:ss') + ' (predefined range: ' + label + ')');
        });

        /*
        初始化搜索内容
       */
        function initSearchContent() {
            var createDate = null;
            if (typeof(Storage) !== "undefined") {
                createDate = sessionStorage.getItem(webStorageKey.CREATE_DATE);
            }
            if (createDate !== null) {
                param.createDate = createDate;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var createDate = null;
            if (typeof(Storage) !== "undefined") {
                createDate = sessionStorage.getItem(webStorageKey.CREATE_DATE);
            }
            if (createDate !== null) {
                $(getParamId().createDate).val(createDate);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().createDate).val('');
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

        /*
         添加
         */
        $('#journal_add').click(function () {
            $.address.value(getAjaxUrl().add + '?id=' + init_page_param.internshipReleaseId + '&studentId=' + init_page_param.studentId);
        });

        /*
         下载 全部
         */
        $('#journal_download_all').click(function () {
            window.location.href = web_path + getAjaxUrl().downloads + '?id=' + init_page_param.internshipReleaseId + '&studentId=' + init_page_param.studentId;
        });

        /*
         批量删除
         */
        $('#journal_dels').click(function () {
            var journalIds = [];
            var ids = $('input[name="check"]:checked');
            for (var i = 0; i < ids.length; i++) {
                journalIds.push($(ids[i]).val());
            }
            journal_dels(journalIds);
        });

        /*
         查看页面
         */
        function look(journalId) {
            $.address.value(getAjaxUrl().look + '?id=' + journalId);
        }

        /*
         编辑页面
         */
        function edit(journalId) {
            $.address.value(getAjaxUrl().edit + '?id=' + journalId);
        }

        /*
         下载
         */
        function download(journalId) {
            window.location.href = web_path + getAjaxUrl().download + '?id=' + journalId;
        }

        /*
         删除
         */
        function journal_del(journalId) {
            var msg;
            msg = Messenger().post({
                message: "确定删除日志吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(journalId);
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
        function journal_dels(journalIds) {
            var msg;
            msg = Messenger().post({
                message: "确定删除选中日志吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            dels(journalIds);
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

        function del(journalId) {
            sendDelAjax(journalId, '删除');
        }

        function dels(journalIds) {
            sendDelAjax(journalIds.join(','), '批量删除');
        }

        /**
         * 删除ajax
         * @param journalId
         * @param message
         */
        function sendDelAjax(journalId, message) {
            Messenger().run({
                successMessage: message + '日志成功',
                errorMessage: message + '日志失败',
                progressMessage: '正在' + message + '日志....'
            }, {
                url: web_path + getAjaxUrl().del,
                type: 'post',
                data: {journalIds: journalId},
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