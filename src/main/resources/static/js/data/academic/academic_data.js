/**
 * Created by lenovo on 2017/4/21.
 */
require(["jquery", "handlebars", "datatables.responsive", "check.all", "jquery.address", "bootstrap", "messenger", "bootstrap-maxlength"],
    function ($, Handlebars) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                academics: '/web/data/academic/data',
                save: '/web/data/academic/save',
                save_valid: '/web/data/academic/save/valid',
                update: '/web/data/academic/update',
                update_valid: '/web/data/academic/update/valid'
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
                "url": web_path + getAjaxUrl().academics,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = getParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": "academicTitleId"},
                {"data": "academicTitleName"},
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
                                    "id": c.academicTitleId,
                                    "value": c.academicTitleName
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
            }
        });

        var html = '<input type="text" id="search_academic" class="form-control input-sm" placeholder="职称" />' +
            '  <button type="button" id="search" class="btn btn-outline btn-default btn-sm"><i class="fa fa-search"></i>搜索</button>' +
            '  <button type="button" id="reset_search" class="btn btn-outline btn-default btn-sm"><i class="fa fa-repeat"></i>重置</button>';
        $('#mytoolbox').append(html);

        var global_button = '<button type="button" id="academic_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                academicTitleName: '#search_academic',
                addAcademic: '#addAcademicTitleName',
                updateAcademicId: '#updateAcademicTitleId',
                updateAcademic: '#updateAcademicTitleName'
            };
        }

        /*
         参数
         */
        var param = {
            academicTitleName: '',
            addAcademic: '',
            updateAcademicId: '',
            updateAcademic: ''
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
            param.academicTitleName = $(getParamId().academicTitleName).val();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().academicTitleName).val('');
        }

        $(getParamId().academicTitleName).keyup(function (event) {
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
                add_academic: '#valid_add_academic',
                update_academic: '#valid_update_academic'
            };
        }

        /*
         错误消息id
         */
        function getErrorMsgId() {
            return {
                add_academic: '#add_academic_title_name_error_msg',
                update_academic: '#update_academic_title_name_error_msg'
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
            $(getParamId().addAcademic).maxlength({
                alwaysShow: false,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });

            $(getParamId().updateAcademic).maxlength({
                alwaysShow: false,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        /*
         即时检验职称
         */
        $(getParamId().addAcademic).blur(function () {
            var academic = $(getParamId().addAcademic).val();
            if (academic.length <= 0 || academic.length > 30) {
                validErrorDom(getValidId().add_academic, getErrorMsgId().add_academic, '职称30个字符以内');
            } else {
                // 职称是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().save_valid,
                    type: 'post',
                    data: {academicTitleName: academic},
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(getValidId().add_academic, getErrorMsgId().add_academic);
                        } else {
                            validErrorDom(getValidId().add_academic, getErrorMsgId().add_academic, data.msg);
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

        $(getParamId().updateAcademic).blur(function () {
            var academic = $(getParamId().updateAcademic).val();
            if (academic.length <= 0 || academic.length > 30) {
                validErrorDom(getValidId().update_academic, getErrorMsgId().update_academic, '职称30个字符以内');
            } else {
                // 职称是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().update_valid,
                    type: 'post',
                    data: {academicTitleId: $(getParamId().updateAcademicId).val(), academicTitleName: academic},
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(getValidId().update_academic, getErrorMsgId().update_academic);
                        } else {
                            validErrorDom(getValidId().update_academic, getErrorMsgId().update_academic, data.msg);
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
        $('#academic_add').click(function () {
            $('#addAcademicModal').modal('show');
        });

        $('#add_academic').click(function () {
            validSave();
        });

        /*
         编辑页面
         */
        function edit(academicTitleId, academicTitleName) {
            $(getParamId().updateAcademicId).val(academicTitleId);
            $(getParamId().updateAcademic).val(academicTitleName);
            $('#updateAcademicModal').modal('show');
        }

        $('#update_academic').click(function () {
            validUpdate();
        });

        function validSave() {
            var academic = $(getParamId().addAcademic).val();
            if (academic.length <= 0 || academic.length > 30) {
                validErrorDom(getValidId().add_academic, getErrorMsgId().add_academic, '职称30个字符以内');
            } else {
                $.post(web_path + getAjaxUrl().save_valid, $('#add_form').serialize(), function (data) {
                    if (data.state) {
                        validSuccessDom(getValidId().add_academic, getErrorMsgId().add_academic);
                        sendAjax('保存', web_path + getAjaxUrl().save, '#add_form');
                    } else {
                        validErrorDom(getValidId().add_academic, getErrorMsgId().add_academic, data.msg);
                    }
                });
            }
        }

        function validUpdate() {
            var academic = $(getParamId().updateAcademic).val();
            if (academic.length <= 0 || academic.length > 30) {
                validErrorDom(getValidId().update_academic, getErrorMsgId().update_academic, '职称30个字符以内');
            } else {
                $.post(web_path + getAjaxUrl().update_valid, {
                    academicTitleId: $(getParamId().updateAcademicId).val(),
                    academicTitleName: academic
                }, function (data) {
                    if (data.state) {
                        validSuccessDom(getValidId().update_academic, getErrorMsgId().update_academic);
                        sendAjax('更新', web_path + getAjaxUrl().update, '#update_form');
                    } else {
                        validErrorDom(getValidId().update_academic, getErrorMsgId().update_academic, data.msg);
                    }
                });
            }
        }

        function sendAjax(message, url, id) {
            Messenger().run({
                successMessage: message + '职称成功',
                errorMessage: message + '职称失败',
                progressMessage: '正在' + message + '职称....'
            }, {
                url: url,
                type: 'post',
                data: $(id).serialize(),
                success: function (data) {
                    if (data.state) {
                        myTable.ajax.reload();
                        validCleanDom(getValidId().add_academic, getErrorMsgId().add_academic);
                        validCleanDom(getValidId().update_academic, getErrorMsgId().update_academic);
                        $('#addAcademicModal').modal('hide');
                        $('#updateAcademicModal').modal('hide');
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