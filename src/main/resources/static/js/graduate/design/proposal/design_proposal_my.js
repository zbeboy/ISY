/**
 * Created by zbeboy on 2017/6/22.
 */
//# sourceURL=graduate_design_proposal_my.js
require(["jquery", "handlebars", "nav_active", "datatables.responsive", "jquery.address",
        "messenger", "bootstrap", "jquery.fileupload-validate"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/proposal/my/data',
                datum_type: '/use/graduate/design/proposal/datums',
                file_upload_url: '/web/graduate/design/proposal/my/save',
                del: '/web/graduate/design/proposal/del',
                download:'/web/graduate/design/proposal/download',
                back: '/web/menu/graduate/design/proposal'
            };
        }

        // 刷新时选中菜单
        nav_active(getAjaxUrl().back);

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
            "aaSorting": [[3, 'desc']],// 排序
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
                {"data": "originalFileName"},
                {"data": "graduationDesignDatumTypeName"},
                {"data": "version"},
                {"data": "updateTimeStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 1,
                    render: function (a, b, c, d) {
                        return c.originalFileName + '.' + c.ext;
                    }
                },
                {
                    targets: 4,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context =
                            {
                                func: [
                                    {
                                        "name": "删除",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.graduationDesignDatumId,
                                        "fileName": c.originalFileName
                                    }, {
                                        "name": "下载",
                                        "css": "download",
                                        "type": "default",
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
                tableElement.delegate('.download', "click", function () {
                    download($(this).attr('data-id'));
                });

                tableElement.delegate('.del', "click", function () {
                    proposal_del($(this).attr('data-id'), $(this).attr('data-original-file-name'));
                });
            }
        });

        var global_button = '<button type="button" id="proposal_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
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
            $(getParamId().graduationDesignDatumTypeName).val(0);
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
         返回
         */
        $('#page_back').click(function () {
            $.address.value(getAjaxUrl().back);
        });

        /*
         添加
         */
        $('#proposal_add').click(function () {
            showUploadModal();
        });

        /*
         下载
         */
        function download(graduationDesignDatumId) {
            window.location.href = getAjaxUrl().download + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignDatumId=' + graduationDesignDatumId;
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
            sendDelAjax(graduationDesignDatumId, '删除', 1);
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
            graduationDesignReleaseId: init_page_param.graduationDesignReleaseId
        };

        // 添加参数id
        var addParamId = {
            graduationDesignDatumTypeId: '#select_datum_type',
            version: '#version',
            fileName: '#fileName',
            fileSize: '#fileSize'
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
        }

        init();

        function init() {
            initDatumType();
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
         * 题目类型数据
         * @param data 数据
         */
        function datumTypeData(data) {
            var template = Handlebars.compile($("#datum-type-template").html());
            $(getParamId().graduationDesignDatumTypeName).html(template(data));
            $(addParamId.graduationDesignDatumTypeId).html(template(data));
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
                closeUploadModal();// 清空信息
                myTable.ajax.reload();
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