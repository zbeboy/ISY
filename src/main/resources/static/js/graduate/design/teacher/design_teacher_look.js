/**
 * Created by lenovo on 2017/5/8.
 */
require(["jquery", "nav_active", "datatables.responsive", "check.all", "jquery.address", "messenger"],
    function ($, nav_active) {

        /*
        参数
       */
        var param = {
            realName: '',
            staffUsername: '',
            staffNumber: ''
        };

        /*
       web storage key.
       */
        var webStorageKey = {
            REAL_NAME: 'GRADUATE_DESIGN_TEACHER_LOOK_REAL_NAME_SEARCH_' + init_page_param.graduationDesignReleaseId,
            STAFF_USERNAME: 'GRADUATE_DESIGN_TEACHER_LOOK_STAFF_USERNAME_SEARCH_' + init_page_param.graduationDesignReleaseId,
            STAFF_NUMBER: 'GRADUATE_DESIGN_TEACHER_LOOK_STAFF_NUMBER_SEARCH_' + init_page_param.graduationDesignReleaseId
        };

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                datas: '/web/graduate/design/tutor/look/data',
                add: '/web/graduate/design/tutor/add',
                back: '/web/menu/graduate/design/tutor',
                condition: '/web/graduate/design/tutor/condition'
            };
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().back);

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(getAjaxUrl().back);
        });

        init();

        function init() {
            if (init_page_param.graduationDesignDeclareData != null
                && init_page_param.graduationDesignDeclareData !== '') {
                $('#overview').removeClass('hidden');
                $('#overview_department').text(init_page_param.graduationDesignDeclareData.departmentName);
                $('#overview_science').text(init_page_param.graduationDesignDeclareData.scienceName);
                var organizeNames = init_page_param.graduationDesignDeclareData.organizeNames.split("###");
                var organizePeoples = init_page_param.graduationDesignDeclareData.organizePeoples.split("###");
                var totalPeoples = 0;
                for (var i = 0; i < organizeNames.length; i++) {
                    totalPeoples += Number(organizePeoples[i]);
                    $('#overview_organize').append($('<div>').append($('<span>').text(organizeNames[i])).append($('<span>').text('  人数:' + organizePeoples[i] + '人')));
                }

                $('#overview_total').text(totalPeoples + '人');
            }
        }

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
                "url": web_path + getAjaxUrl().datas,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                }
            },
            "columns": [
                {"data": "staffRealName"},
                {"data": "staffNumber"},
                {"data": "staffUsername"},
                {"data": "studentCount"},
                {"data": "assignerName"}
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-8'>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button = '<button type="button" id="teacher_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                realName: '#search_staff_real_name',
                staffUsername: '#search_staff_username',
                staffNumber: '#search_staff_number'
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
            param.realName = $(getParamId().realName).val();
            param.staffUsername = $(getParamId().staffUsername).val();
            param.staffNumber = $(getParamId().staffNumber).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.REAL_NAME, param.realName);
                sessionStorage.setItem(webStorageKey.STAFF_USERNAME, param.staffUsername);
                sessionStorage.setItem(webStorageKey.STAFF_NUMBER, param.staffNumber);
            }
        }

        /*
       初始化搜索内容
      */
        function initSearchContent() {
            var realName = null;
            var staffUsername = null;
            var staffNumber = null;
            if (typeof(Storage) !== "undefined") {
                realName = sessionStorage.getItem(webStorageKey.REAL_NAME);
                staffUsername = sessionStorage.getItem(webStorageKey.STAFF_USERNAME);
                staffNumber = sessionStorage.getItem(webStorageKey.STAFF_NUMBER);
            }
            if (realName !== null) {
                param.realName = realName;
            }

            if (staffUsername !== null) {
                param.staffUsername = staffUsername;
            }

            if (staffNumber !== null) {
                param.staffNumber = staffNumber;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var realName = null;
            var staffUsername = null;
            var staffNumber = null;
            if (typeof(Storage) !== "undefined") {
                realName = sessionStorage.getItem(webStorageKey.REAL_NAME);
                staffUsername = sessionStorage.getItem(webStorageKey.STAFF_USERNAME);
                staffNumber = sessionStorage.getItem(webStorageKey.STAFF_NUMBER);
            }
            if (realName !== null) {
                $(getParamId().realName).val(realName);
            }

            if (staffUsername !== null) {
                $(getParamId().staffUsername).val(staffUsername);
            }

            if (staffNumber !== null) {
                $(getParamId().staffNumber).val(staffNumber);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().realName).val('');
            $(getParamId().staffUsername).val('');
            $(getParamId().staffNumber).val('');
        }

        $(getParamId().realName).keyup(function (event) {
            if (event.keyCode === 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().staffUsername).keyup(function (event) {
            if (event.keyCode === 13) {
                initParam();
                myTable.ajax.reload();
            }
        });

        $(getParamId().staffNumber).keyup(function (event) {
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
        $('#teacher_add').click(function () {
            $.post(getAjaxUrl().condition, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    $.address.value(getAjaxUrl().add + '?rId=' + init_page_param.graduationDesignReleaseId);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        });
    });