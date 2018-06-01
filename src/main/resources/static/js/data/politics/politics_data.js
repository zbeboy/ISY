/**
 * Created by lenovo on 2016-11-03.
 */
require(["jquery", "handlebars", "datatables.responsive", "check.all", "jquery.address", "bootstrap", "messenger", "bootstrap-maxlength"],
    function ($, Handlebars) {
        /*
         参数
        */
        var param = {
            politicalLandscapeName: ''
        };

        /*
        web storage key.
       */
        var webStorageKey = {
            POLITICAL_LANDSCAPE_NAME: 'DATA_POLITICS_LANDSCAPE_NAME_SEARCH'
        };

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                politics: '/web/data/politics/data',
                save: '/web/data/politics/save',
                save_valid: '/web/data/politics/save/valid',
                update: '/web/data/politics/update',
                update_valid: '/web/data/politics/update/valid'
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
            },
            searching: false,
            "processing": true, // 打开数据加载时的等待效果
            "serverSide": true,// 打开后台分页
            "ajax": {
                "url": web_path + getAjaxUrl().politics,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    initSearchContent();
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": "politicalLandscapeId"},
                {"data": "politicalLandscapeName"},
                {"data": null}
            ],
            columnDefs: [
                {
                    targets: 2,
                    orderable: false,
                    render: function (a, b, c, d) {
                        var context =
                        {
                            func: [
                                {
                                    "name": "编辑",
                                    "css": "edit",
                                    "type": "primary",
                                    "id": c.politicalLandscapeId,
                                    "value": c.politicalLandscapeName
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-4'><'col-sm-6'<'#mytoolbox'>>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'), $(this).attr('data-value'));
                });
                // 初始化搜索框中内容
                initSearchInput();
            }
        });

        var html = '<input type="text" id="search_politics" class="form-control input-sm" placeholder="政治面貌" />' +
            '  <button type="button" id="search" class="btn btn-outline btn-default btn-sm"><i class="fa fa-search"></i>搜索</button>' +
            '  <button type="button" id="reset_search" class="btn btn-outline btn-default btn-sm"><i class="fa fa-repeat"></i>重置</button>';
        $('#mytoolbox').append(html);

        var global_button = '<button type="button" id="politics_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                politicalLandscapeName: '#search_politics',
                addPolitics: '#addPoliticalLandscapeName',
                updatePoliticalLandscapeId: '#updatePoliticalLandscapeId',
                updatePolitics: '#updatePoliticalLandscapeName'
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
            param.politicalLandscapeName = $(getParamId().politicalLandscapeName).val();
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.POLITICAL_LANDSCAPE_NAME, param.politicalLandscapeName);
            }
        }

        /*
       初始化搜索内容
        */
        function initSearchContent() {
            var politicalLandscapeName = null;
            if (typeof(Storage) !== "undefined") {
                politicalLandscapeName = sessionStorage.getItem(webStorageKey.POLITICAL_LANDSCAPE_NAME);
            }
            if (politicalLandscapeName !== null) {
                param.politicalLandscapeName = politicalLandscapeName;
            }
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var politicalLandscapeName = null;
            if (typeof(Storage) !== "undefined") {
                politicalLandscapeName = sessionStorage.getItem(webStorageKey.POLITICAL_LANDSCAPE_NAME);
            }
            if (politicalLandscapeName !== null) {
                $(getParamId().politicalLandscapeName).val(politicalLandscapeName);
            }
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().politicalLandscapeName).val('');
        }

        $(getParamId().politicalLandscapeName).keyup(function (event) {
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

        /**
         * 清除检验
         * @param validId
         * @param errorMsgId
         */
        function validCleanDom(validId, errorMsgId) {
            $(validId).removeClass('has-error').removeClass('has-success');
            $(errorMsgId).removeClass('hidden').text('');
        }

        /*
         检验id
         */
        function getValidId() {
            return {
                add_politics: '#valid_add_political_landscape',
                update_politics: '#valid_update_political_landscape'
            };
        }

        /*
         错误消息id
         */
        function getErrorMsgId() {
            return {
                add_politics: '#add_political_landscape_name_error_msg',
                update_politics: '#update_political_landscape_name_error_msg'
            };
        }

        init();

        function init() {
            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            $(getParamId().addPolitics).maxlength({
                alwaysShow: false,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });

            $(getParamId().updatePolitics).maxlength({
                alwaysShow: false,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        /*
         即时检验
         */
        $(getParamId().addPolitics).blur(function () {
            var politics = $(getParamId().addPolitics).val();
            if (politics.length <= 0 || politics.length > 30) {
                validErrorDom(getValidId().add_politics, getErrorMsgId().add_politics, '政治面貌30个字符以内');
            } else {
                // 是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().save_valid,
                    type: 'post',
                    data: {politicalLandscapeName: politics},
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(getValidId().add_politics, getErrorMsgId().add_politics);
                        } else {
                            validErrorDom(getValidId().add_politics, getErrorMsgId().add_politics, data.msg);
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

        $(getParamId().updatePolitics).blur(function () {
            var politics = $(getParamId().updatePolitics).val();
            if (politics.length <= 0 || politics.length > 30) {
                validErrorDom(getValidId().update_politics, getErrorMsgId().update_politics, '政治面貌30个字符以内');
            } else {
                // 是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().update_valid,
                    type: 'post',
                    data: {
                        politicalLandscapeId: $(getParamId().updatePoliticalLandscapeId).val(),
                        politicalLandscapeName: politics
                    },
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(getValidId().update_politics, getErrorMsgId().update_politics);
                        } else {
                            validErrorDom(getValidId().update_politics, getErrorMsgId().update_politics, data.msg);
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

        /*
         添加页面
         */
        $('#politics_add').click(function () {
            $('#addPoliticsModal').modal('show');
        });

        $('#add_politics').click(function () {
            validSave();
        });

        /*
         编辑页面
         */
        function edit(politicsId, politicsName) {
            $(getParamId().updatePoliticalLandscapeId).val(politicsId);
            $(getParamId().updatePolitics).val(politicsName);
            $('#updatePoliticalLandscapeNameModal').modal('show');
        }

        $('#update_politics').click(function () {
            validUpdate();
        });

        function validSave() {
            var politics = $(getParamId().addPolitics).val();
            if (politics.length <= 0 || politics.length > 30) {
                validErrorDom(getValidId().add_politics, getErrorMsgId().add_politics, '政治面貌30个字符以内');
            } else {
                $.post(web_path + getAjaxUrl().save_valid, $('#add_form').serialize(), function (data) {
                    if (data.state) {
                        validSuccessDom(getValidId().add_politics, getErrorMsgId().add_politics);
                        sendAjax('保存', web_path + getAjaxUrl().save, '#add_form');
                    } else {
                        validErrorDom(getValidId().add_politics, getErrorMsgId().add_politics, data.msg);
                    }
                });
            }
        }

        function validUpdate() {
            var politics = $(getParamId().updatePolitics).val();
            if (politics.length <= 0 || politics.length > 30) {
                validErrorDom(getValidId().update_politics, getErrorMsgId().update_politics, '政治面貌30个字符以内');
            } else {
                $.post(web_path + getAjaxUrl().update_valid, {
                    politicalLandscapeId: $(getParamId().updatePoliticalLandscapeId).val(),
                    politicalLandscapeName: politics
                }, function (data) {
                    if (data.state) {
                        validSuccessDom(getValidId().update_politics, getErrorMsgId().update_politics);
                        sendAjax('更新', web_path + getAjaxUrl().update, '#update_form');
                    } else {
                        validErrorDom(getValidId().update_politics, getErrorMsgId().update_politics, data.msg);
                    }
                });
            }
        }

        function sendAjax(message, url, id) {
            Messenger().run({
                successMessage: message + '政治面貌成功',
                errorMessage: message + '政治面貌失败',
                progressMessage: '正在' + message + '政治面貌....'
            }, {
                url: url,
                type: 'post',
                data: $(id).serialize(),
                success: function (data) {
                    if (data.state) {
                        myTable.ajax.reload();
                        validCleanDom(getValidId().add_politics, getErrorMsgId().add_politics);
                        validCleanDom(getValidId().update_politics, getErrorMsgId().update_politics);
                        $('#addPoliticsModal').modal('hide');
                        $('#updatePoliticalLandscapeNameModal').modal('hide');
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