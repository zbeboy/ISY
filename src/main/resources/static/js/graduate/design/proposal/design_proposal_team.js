/**
 * Created by zbeboy on 2017/6/28.
 */
require(["jquery", "handlebars", "constants", "nav_active", "bootstrap-select-zh-CN", "datatables.responsive",
        "jquery.address", "messenger", "bootstrap", "jquery.fileupload-validate"],
    function ($, Handlebars, constants, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/proposal/team/data',
                teachers: '/anyone/graduate/design/teachers',
                datum_type: '/use/graduate/design/proposal/datums',
                datum_info: '/web/graduate/design/proposal/team/datum',
                del: '/web/graduate/design/proposal/del',
                download: '/web/graduate/design/proposal/download',
                file_upload_url: '/web/graduate/design/proposal/team/update',
                back: '/web/menu/graduate/design/proposal'
            };
        }

        /*
         参数id
         */
        function getParamId() {
            return {
                staffId: '#select_staff',
                studentName: '#search_student_name',
                studentNumber: '#search_student_number',
                originalFileName: '#search_file',
                graduationDesignDatumTypeName: '#graduation_design_datum_type'
            };
        }

        /*
         参数
         */
        var param = {
            staffId: init_page_param.staffId,
            studentName: '',
            studentNumber: '',
            originalFileName: '',
            graduationDesignDatumTypeName: ''
        };

        /*
        web storage key.
        */
        var webStorageKey = {
            STUDENT_NAME: 'GRADUATE_DESIGN_PROPOSAL_TEAM_STUDENT_NAME_SEARCH_' + init_page_param.graduationDesignReleaseId,
            STUDENT_NUMBER: 'GRADUATE_DESIGN_PROPOSAL_TEAM_STUDENT_NUMBER_SEARCH_' + init_page_param.graduationDesignReleaseId,
            ORIGINAL_FILE_NAME: 'GRADUATE_DESIGN_PROPOSAL_TEAM_ORIGINAL_FILE_NAME_SEARCH_' + init_page_param.graduationDesignReleaseId,
            GRADUATION_DESIGN_DATUM_TYPE_NAME: 'GRADUATE_DESIGN_PROPOSAL_TEAM_GRADUATION_DESIGN_DATUM_TYPE_NAME_SEARCH_' + init_page_param.graduationDesignReleaseId
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
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "aaSorting": [[6, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                    d.staffId = getParam().staffId;
                }
            },
            "columns": [
                {"data": "realName"},
                {"data": "studentNumber"},
                {"data": "organizeName"},
                {"data": "originalFileName"},
                {"data": "version"},
                {"data": "graduationDesignDatumTypeName"},
                {"data": "updateTimeStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 7,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context = null;
                        var html = '<i class="fa fa-lock"></i>';

                        // 当前用户角色为系统或管理员
                        if (init_page_param.currentUserRoleName === constants.global_role_name.system_role ||
                            init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                            context =
                                {
                                    func: [
                                        {
                                            "name": "替换",
                                            "css": "edit",
                                            "type": "primary",
                                            "id": c.graduationDesignDatumId
                                        },
                                        {
                                            "name": "删除",
                                            "css": "del",
                                            "type": "danger",
                                            "id": c.graduationDesignDatumId
                                        },
                                        {
                                            "name": "下载",
                                            "css": "download",
                                            "type": "default",
                                            "id": c.graduationDesignDatumId
                                        }
                                    ]
                                };
                        } else {
                            // 学生
                            if (init_page_param.usersTypeName === constants.global_users_type.student_type) {
                                if (c.studentId == init_page_param.studentId && init_page_param.studentId != 0) {
                                    context =
                                        {
                                            func: [
                                                {
                                                    "name": "替换",
                                                    "css": "edit",
                                                    "type": "primary",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "删除",
                                                    "css": "del",
                                                    "type": "danger",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "下载",
                                                    "css": "download",
                                                    "type": "default",
                                                    "id": c.graduationDesignDatumId
                                                }
                                            ]
                                        };
                                }
                            } else if (init_page_param.usersTypeName === constants.global_users_type.staff_type) {// 教师
                                if (c.staffId == init_page_param.staffId && init_page_param.staffId != 0) {
                                    context =
                                        {
                                            func: [
                                                {
                                                    "name": "替换",
                                                    "css": "edit",
                                                    "type": "primary",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "删除",
                                                    "css": "del",
                                                    "type": "danger",
                                                    "id": c.graduationDesignDatumId
                                                },
                                                {
                                                    "name": "下载",
                                                    "css": "download",
                                                    "type": "default",
                                                    "id": c.graduationDesignDatumId
                                                }
                                            ]
                                        };
                                } else {
                                    context =
                                        {
                                            func: [
                                                {
                                                    "name": "下载",
                                                    "css": "download",
                                                    "type": "default",
                                                    "id": c.graduationDesignDatumId
                                                }
                                            ]
                                        };
                                }
                            }
                        }
                        if (context != null) {
                            html = template(context);
                        }
                        return html;
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
                    del($(this).attr('data-id'));
                });

                tableElement.delegate('.download', "click", function () {
                    download($(this).attr('data-id'));
                });
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var global_button =
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').html(global_button);


        /*
         初始化参数
         */
        function initParam() {
            param.staffId = $(getParamId().staffId).val();
            param.studentName = $(getParamId().studentName).val();
            param.studentNumber = $(getParamId().studentNumber).val();
            param.originalFileName = $(getParamId().originalFileName).val();
            param.graduationDesignDatumTypeName = $(getParamId().graduationDesignDatumTypeName).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.STUDENT_NAME, param.studentName);
                sessionStorage.setItem(webStorageKey.STUDENT_NUMBER, param.studentNumber);
                sessionStorage.setItem(webStorageKey.ORIGINAL_FILE_NAME, param.originalFileName);
                sessionStorage.setItem(webStorageKey.GRADUATION_DESIGN_DATUM_TYPE_NAME, param.graduationDesignDatumTypeName);
            }
        }

        /*
       初始化搜索内容
      */
        function initSearchContent() {
            var studentName = null;
            var studentNumber = null;
            var originalFileName = null;
            var graduationDesignDatumTypeName = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                originalFileName = sessionStorage.getItem(webStorageKey.ORIGINAL_FILE_NAME);
                graduationDesignDatumTypeName = sessionStorage.getItem(webStorageKey.GRADUATION_DESIGN_DATUM_TYPE_NAME);
            }
            if (studentName !== null) {
                param.studentName = studentName;
            }

            if (studentNumber !== null) {
                param.studentNumber = studentNumber;
            }

            if (originalFileName !== null) {
                param.originalFileName = originalFileName;
            }

            if (graduationDesignDatumTypeName !== null) {
                param.graduationDesignDatumTypeName = graduationDesignDatumTypeName;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var studentName = null;
            var studentNumber = null;
            var originalFileName = null;
            if (typeof(Storage) !== "undefined") {
                studentName = sessionStorage.getItem(webStorageKey.STUDENT_NAME);
                studentNumber = sessionStorage.getItem(webStorageKey.STUDENT_NUMBER);
                originalFileName = sessionStorage.getItem(webStorageKey.ORIGINAL_FILE_NAME);
            }
            if (studentName !== null) {
                $(getParamId().studentName).val(studentName);
            }

            if (studentNumber !== null) {
                $(getParamId().studentNumber).val(studentNumber);
            }

            if (originalFileName !== null) {
                $(getParamId().originalFileName).val(originalFileName);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().studentName).val('');
            $(getParamId().studentNumber).val('');
            $(getParamId().originalFileName).val('');
            $(getParamId().graduationDesignDatumTypeName).val(0);
        }

        $(getParamId().staffId).on('changed.bs.select', function (e) {
            initParam();
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

        pageAop.delegate('#refresh', "click", function () {
            myTable.ajax.reload();
        });

        init();

        function init() {
            initTeachers();
            initDatumType();
        }

        /*
         初始化选中教师
         */
        var selectedTeacherCount = true;

        /**
         * 初始化教师数据
         */
        function initTeachers() {
            $.get(web_path + getAjaxUrl().teachers, {id: init_page_param.graduationDesignReleaseId}, function (data) {
                if (data.state) {
                    teacherData(data);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        /**
         * 初始文件类型数据
         */
        function initDatumType() {
            $.get(web_path + getAjaxUrl().datum_type, function (data) {
                if (data.state) {
                    datumTypeData(data);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        /**
         * 教师数据
         * @param data 数据
         */
        function teacherData(data) {
            var template = Handlebars.compile($("#staff-template").html());
            Handlebars.registerHelper('staff_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.staffId));
            });
            Handlebars.registerHelper('staff_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName + ' ' + this.staffMobile));
            });
            $(getParamId().staffId).html(template(data));

            // 只在页面初始化加载一次
            if (selectedTeacherCount) {
                selectedTeacher();
                selectedTeacherCount = false;
            }
        }

        /**
         * 选中教师
         */
        function selectedTeacher() {
            var realStaffId = init_page_param.staffId;
            var staffChildrens = $(getParamId().staffId).children();
            for (var i = 0; i < staffChildrens.length; i++) {
                if (Number($(staffChildrens[i]).val()) === realStaffId) {
                    $(staffChildrens[i]).prop('selected', true);
                    break;
                }
            }

            // 选择教师
            $(getParamId().staffId).selectpicker({
                liveSearch: true
            });
        }

        /**
         * 题目类型数据
         * @param data 数据
         */
        function datumTypeData(data) {
            var template = Handlebars.compile($("#datum-type-template").html());
            $(getParamId().graduationDesignDatumTypeName).html(template(data));
            $(addParamId.graduationDesignDatumTypeId).html(template(data));

            var graduationDesignDatumTypeName = null;
            if (typeof(Storage) !== "undefined") {
                graduationDesignDatumTypeName = sessionStorage.getItem(webStorageKey.GRADUATION_DESIGN_DATUM_TYPE_NAME);
            }
            if (graduationDesignDatumTypeName !== null) {
                $(getParamId().graduationDesignDatumTypeName).val(graduationDesignDatumTypeName);
            }
        }

        /**
         * 编辑
         * @param graduationDesignDatumId
         */
        function edit(graduationDesignDatumId) {
            $.post(web_path + getAjaxUrl().datum_info, {
                id: init_page_param.graduationDesignReleaseId,
                graduationDesignDatumId: graduationDesignDatumId
            }, function (data) {
                if (data.state) {
                    $(addParamId.graduationDesignDatumTypeId).val(data.objectResult.graduationDesignDatumTypeId);
                    $(addParamId.graduationDesignDatumTypeId).prop('disabled', true);
                    $(addParamId.version).val(data.objectResult.version);
                    $(addParamId.fileName).text(data.objectResult.originalFileName);
                    $(addParamId.fileSize).text(data.objectResult.size);
                    $(addParamId.graduationDesignDatumId).val(graduationDesignDatumId);
                    showUploadModal();
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        /**
         * 删除
         * @param graduationDesignDatumId
         */
        function del(graduationDesignDatumId) {
            var msg;
            msg = Messenger().post({
                message: "确定删除文件吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            sendDelAjax(graduationDesignDatumId, '删除');
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

        /**
         * 删除
         * @param graduationDesignDatumId
         */
        function download(graduationDesignDatumId) {
            window.location.href = getAjaxUrl().download + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignDatumId=' + graduationDesignDatumId;
        }

        /**
         * 删除ajax
         * @param graduationDesignDatumId
         * @param message
         */
        function sendDelAjax(graduationDesignDatumId, message) {
            Messenger().run({
                successMessage: message + '文件成功',
                errorMessage: message + '文件失败',
                progressMessage: '正在' + message + '文件....'
            }, {
                url: web_path + getAjaxUrl().del,
                type: 'post',
                data: {
                    id: init_page_param.graduationDesignReleaseId,
                    graduationDesignDatumId: graduationDesignDatumId
                },
                success: function (data) {
                    if (data.state) {
                        myTable.ajax.reload();
                        console.log('haha')
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

        // 添加参数
        var addParam = {
            graduationDesignDatumTypeId: '',
            version: '',
            graduationDesignReleaseId: init_page_param.graduationDesignReleaseId,
            graduationDesignDatumId: ''
        };

        // 添加参数id
        var addParamId = {
            graduationDesignDatumTypeId: '#select_datum_type',
            version: '#version',
            fileName: '#fileName',
            fileSize: '#fileSize',
            graduationDesignDatumId: '#graduationDesignDatumId'
        };

        // 检验id
        var addParamValidId = {
            graduationDesignDatumTypeId: '#valid_datum_type',
            version: '#valid_version'
        };

        // 错误id
        var addParamErrorId = {
            graduationDesignDatumTypeId: '#datum_type_error_msg',
            version: '#version_error_msg'
        };

        /**
         * 检验成功
         * @param validId
         * @param errorMsgId
         */
        function validSuccessDom(validId, errorMsgId) {
            $(validId).addClass('has-success').removeClass('has-error');
            $(errorMsgId).addClass('hidden').text('');
        }

        /**
         * 检验失败
         * @param validId
         * @param errorMsgId
         * @param msg
         */
        function validErrorDom(validId, errorMsgId, msg) {
            $(validId).addClass('has-error').removeClass('has-success');
            $(errorMsgId).removeClass('hidden').text(msg);
        }

        /*
         清除验证
         */
        function validCleanDom(inputId, errorId) {
            $(inputId).removeClass('has-error').removeClass('has-success');
            $(errorId).addClass('hidden').text('');
        }

        /**
         * 初始化添加参数
         */
        function initAddParam() {
            addParam.graduationDesignDatumTypeId = $(addParamId.graduationDesignDatumTypeId).val();
            addParam.version = $(addParamId.version).val();
            addParam.graduationDesignDatumId = $(addParamId.graduationDesignDatumId).val();
        }


        /*
         即时检验
         */
        $(addParamId.graduationDesignDatumTypeId).change(function () {
            initAddParam();
            var graduationDesignDatumTypeId = addParam.graduationDesignDatumTypeId;
            if (Number(graduationDesignDatumTypeId) <= 0) {
                validErrorDom(addParamValidId.graduationDesignDatumTypeId, addParamErrorId.graduationDesignDatumTypeId, '请选择题目类型');
            } else {
                validSuccessDom(addParamValidId.graduationDesignDatumTypeId, addParamErrorId.graduationDesignDatumTypeId);
            }
        });

        $(addParamId.version).change(function () {
            initAddParam();
            var version = addParam.version;
            if (version !== '') {
                if (version.length <= 0 || version.length > 10) {
                    validErrorDom(addParamValidId.version, addParamErrorId.version, '版本应为10个字符之间');
                } else {
                    validSuccessDom(addParamValidId.version, addParamErrorId.version);
                }
            }
        });

        /**
         * 展开上传文件modal
         */
        function showUploadModal() {
            $('#uploadModal').modal('show');
        }

        /**
         * 关闭文件上传modal
         */
        function closeUploadModal() {
            $(addParamId.graduationDesignDatumTypeId).val(0);
            $(addParamId.version).val('');
            $(addParamId.fileName).text('');
            $(addParamId.fileSize).text('');
            validCleanDom(addParamValidId.graduationDesignDatumTypeId, addParamErrorId.graduationDesignDatumTypeId);
            validCleanDom(addParamValidId.version, addParamErrorId.version);
            $('#uploadModal').modal('hide');
        }

        var startUpload = null; // 开始上传

        // 上传组件
        $('#fileupload').fileupload({
            url: web_path + getAjaxUrl().file_upload_url,
            dataType: 'json',
            maxFileSize: 100000000,// 100MB
            acceptFileTypes: /([.\/])(doc|docx|xls|xlsx|ppt|pptx)$/i,
            formAcceptCharset: 'utf-8',
            autoUpload: false,// 关闭自动上传
            maxNumberOfFiles: 1,
            messages: {
                maxNumberOfFiles: '最大支持上传1个文件',
                acceptFileTypes: '仅支持上传doc,docx,xls,xlsx,ppt,pptx等类型文件',
                maxFileSize: '单文件上传仅允许100MB大小'
            },
            add: function (e, data) {
                $(addParamId.fileName).text(data.files[0].name);
                $(addParamId.fileSize).text(data.files[0].size);
                startUpload = data;
            },
            submit: function (e, data) {
                data.formData = addParam;
            },
            done: function (e, data) {
                if (data.result.state) {
                    Messenger().post({
                        message: data.result.msg,
                        type: 'success',
                        showCloseButton: true
                    });
                    closeUploadModal();// 清空信息
                    myTable.ajax.reload();
                } else {
                    Messenger().post({
                        message: data.result.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            }
        }).on('fileuploadsubmit', function (evt, data) {
            var isOk = true;
            var $this = $(this);
            var validation = data.process(function () {
                return $this.fileupload('process', data);
            });
            validation.fail(function (data) {
                isOk = false;
                Messenger().post({
                    message: '上传失败: ' + data.files[0].error,
                    type: 'error',
                    showCloseButton: true
                });
            });
            return isOk;
        });

        function validGraduationDesignDatumTypeId() {
            var graduationDesignDatumTypeId = addParam.graduationDesignDatumTypeId;
            if (Number(graduationDesignDatumTypeId) <= 0) {
                Messenger().post({
                    message: '请选择题目类型',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validVersion();
            }
        }

        function validVersion() {
            var version = addParam.version;
            if (version !== '') {
                if (version.length > 10) {
                    Messenger().post({
                        message: '版本应为10个字符之间',
                        type: 'error',
                        showCloseButton: true
                    });
                } else {
                    validUpload();
                }
            } else {
                validUpload();
            }
        }

        function validUpload() {
            var fileName = $(addParamId.fileName).text();
            if (fileName !== '') {
                $(addParamId.graduationDesignDatumTypeId).prop('disabled', false);
                startUpload.submit();
            } else {
                Messenger().post({
                    message: '请选择文件',
                    type: 'error',
                    showCloseButton: true
                });
            }
        }

        // 确认上传
        $('#confirmUpload').click(function () {
            initAddParam();
            validGraduationDesignDatumTypeId();
        });

        // 取消上传
        $('#cancelUpload').click(function () {
            closeUploadModal();// 清空信息
        });

    });