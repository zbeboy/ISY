/**
 * Created by zbeboy on 2017/9/27.
 */
//# sourceURL=design_proposal_group.js
require(["jquery", "handlebars", "nav_active", "datatables.responsive", "jquery.address",
        "messenger", "bootstrap", "jquery.fileupload-validate"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                data_url: '/web/graduate/design/proposal/group/data',
                file_upload_url: '/web/graduate/design/proposal/group/save',
                del: '/web/graduate/design/proposal/group/del',
                download: '/web/graduate/design/proposal/group/download',
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
            "aaSorting": [[1, 'desc']],// 排序
            "ajax": {
                "url": web_path + getAjaxUrl().data_url,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                    d.graduationDesignReleaseId = init_page_param.graduationDesignReleaseId;
                    d.graduationDesignTeacherId = init_page_param.graduationDesignTeacherId;
                }
            },
            "columns": [
                {"data": "originalFileName"},
                {"data": "uploadTimeStr"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 0,
                    render: function (a, b, c, d) {
                        return c.originalFileName + '.' + c.ext;
                    }
                },
                {
                    targets: 2,
                    orderable: false,
                    render: function (a, b, c, d) {

                        var context = {};

                        if (init_page_param.currUseIsStaff === 1) {
                            context = {
                                func: [
                                    {
                                        "name": "删除",
                                        "css": "del",
                                        "type": "danger",
                                        "id": c.graduationDesignDatumGroupId,
                                        "fileName": c.originalFileName
                                    }, {
                                        "name": "下载",
                                        "css": "download",
                                        "type": "default",
                                        "id": c.graduationDesignDatumGroupId,
                                        "fileName": c.originalFileName
                                    }
                                ]
                            };
                        } else {
                            context = {
                                func: [
                                    {
                                        "name": "下载",
                                        "css": "download",
                                        "type": "default",
                                        "id": c.graduationDesignDatumGroupId,
                                        "fileName": c.originalFileName
                                    }
                                ]
                            };
                        }

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
                    group_del($(this).attr('data-id'), $(this).attr('data-original-file-name'));
                });
            }
        });

        var global_button = '';
        if (init_page_param.currUseIsStaff === 1) {
            global_button = '<button type="button" id="group_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
                '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        } else {
            global_button = '<button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        }
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                originalFileName: '#search_file'
            };
        }

        /*
         参数
         */
        var param = {
            originalFileName: ''
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
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().originalFileName).val('');
        }

        $(getParamId().originalFileName).keyup(function (event) {
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
         返回
         */
        $('#page_back').click(function () {
            $.address.value(getAjaxUrl().back);
        });

        /*
         添加
         */
        $('#group_add').click(function () {
            showUploadModal();
        });

        /*
         下载
         */
        function download(graduationDesignDatumGroupId) {
            window.location.href = getAjaxUrl().download + '?id=' + init_page_param.graduationDesignReleaseId + '&graduationDesignDatumGroupId=' + graduationDesignDatumGroupId;
        }

        /*
         删除
         */
        function group_del(graduationDesignDatumGroupId, originalFileName) {
            var msg;
            msg = Messenger().post({
                message: "确定删除文件 '" + originalFileName + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(graduationDesignDatumGroupId);
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

        function del(graduationDesignDatumGroupId) {
            sendDelAjax(graduationDesignDatumGroupId, '删除');
        }

        /**
         * 删除ajax
         * @param graduationDesignDatumGroupId
         * @param message
         */
        function sendDelAjax(graduationDesignDatumGroupId, message) {
            Messenger().run({
                successMessage: message + '文件成功',
                errorMessage: message + '文件失败',
                progressMessage: '正在' + message + '文件....'
            }, {
                url: web_path + getAjaxUrl().del,
                type: 'post',
                data: {
                    id: init_page_param.graduationDesignReleaseId,
                    graduationDesignDatumGroupId: graduationDesignDatumGroupId
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
            graduationDesignReleaseId: init_page_param.graduationDesignReleaseId
        };

        // 添加参数id
        var addParamId = {
            fileName: '#fileName',
            fileSize: '#fileSize'
        };

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
            $(addParamId.fileName).text('');
            $(addParamId.fileSize).text('');
            $('#uploadModal').modal('hide');
        }

        var startUpload = null; // 开始上传

        // 上传组件
        $('#fileupload').fileupload({
            url: web_path + getAjaxUrl().file_upload_url,
            dataType: 'json',
            maxFileSize: 100000000,// 100MB
            acceptFileTypes: /([.\/])(zip|rar|doc|docx|xls|xlsx|ppt|pptx)$/i,
            formAcceptCharset: 'utf-8',
            autoUpload: false,// 关闭自动上传
            maxNumberOfFiles: 1,
            messages: {
                maxNumberOfFiles: '最大支持上传1个文件',
                acceptFileTypes: '仅支持上传zip,rar,doc,docx,xls,xlsx,ppt,pptx等类型文件',
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

        function upload() {
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
            upload();
        });

        // 取消上传
        $('#cancelUpload').click(function () {
            closeUploadModal();// 清空信息
        });

    });