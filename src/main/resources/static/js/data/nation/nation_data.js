/**
 * Created by lenovo on 2016-11-02.
 */
require(["jquery", "messenger", "handlebars", "datatables.responsive", "check.all", "jquery.address","bootstrap"],
    function ($, messenger, Handlebars, dt, checkall, jqueryAddress,bootstrap) {

        /*
         ajax url
         */
        function getAjaxUrl() {
            return {
                nations: '/web/data/nation/data',
                save: '/web/data/nation/save',
                save_valid: '/web/data/nation/save/valid',
                update: '/web/data/nation/update',
                update_valid: '/web/data/nation/update/valid'
            };
        }

        var operator_button = $("#operator_button").html();
        // 预编译模板
        var template = Handlebars.compile(operator_button);

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
                "url": web_path + getAjaxUrl().nations,
                "dataSrc": "data",
                "data": function (d) {
                    // 添加额外的参数传给服务器
                    var searchParam = initParam();
                    d.extra_search = JSON.stringify(searchParam);
                }
            },
            "columns": [
                {"data": "nationId"},
                {"data": "nationName"},
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
                                    "id": c.nationId,
                                    "value": c.nationName
                                }
                            ]
                        };
                        var html = template(context);
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
            "dom": "<'row'<'col-sm-2'l><'#global_button.col-sm-4'><'col-sm-6'<'#mytoolbox'>>r>" +
            "t" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            initComplete: function () {
                tableElement.delegate('.edit', "click", function () {
                    edit($(this).attr('data-id'),$(this).attr('data-value'));
                });
            }
        });

        var html = '<input type="text" id="search_nation" class="form-control input-sm" placeholder="民族" />' +
            '  <button type="button" id="search" class="btn btn-outline btn-default btn-sm"><i class="fa fa-search"></i>搜索</button>' +
            '  <button type="button" id="reset_search" class="btn btn-outline btn-default btn-sm"><i class="fa fa-repeat"></i>重置</button>';
        $('#mytoolbox').append(html);

        var global_button = '<button type="button" id="nation_add" class="btn btn-outline btn-primary btn-sm"><i class="fa fa-plus"></i>添加</button>' +
            '  <button type="button" id="refresh" class="btn btn-outline btn-default btn-sm"><i class="fa fa-refresh"></i>刷新</button>';
        $('#global_button').append(global_button);

        /*
         参数id
         */
        function getParamId() {
            return {
                nationName: '#search_nation',
                addNation:'#addNationName',
                updateNationId:'#updateNationId',
                updateNation:'#updateNationName'
            };
        }

        /*
         初始化参数
         */
        function initParam() {
            return {
                nationName: $(getParamId().nationName).val(),
                nation:$(getParamId().nation).val()
            };
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(getParamId().nationName).val('');
        }

        $(getParamId().nationName).keyup(function (event) {
            if (event.keyCode == 13) {
                myTable.ajax.reload();
            }
        });

        $('#search').click(function () {
            myTable.ajax.reload();
        });

        $('#reset_search').click(function () {
            cleanParam();
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
        function getValidId(){
            return {
                add_nation: '#valid_add_nation',
                update_nation:'#valid_update_nation'
            };
        }

        /*
         错误消息id
         */
        function getErrorMsgId(){
            return {
                add_nation: '#add_nation_name_error_msg',
                update_nation:'#update_nation_name_error_msg'
            };
        }

        /*
         即时检验民族
         */
        $(getParamId().addNation).blur(function () {
            var nation = $(getParamId().addNation).val();
            if (nation.length <= 0 || nation.length > 30) {
                validErrorDom(getValidId().add_nation, getErrorMsgId().add_nation, '民族30个字符以内');
            } else {
                // 民族是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().save_valid,
                    type: 'post',
                    data: {nationName:nation},
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(getValidId().add_nation, getErrorMsgId().add_nation);
                        } else {
                            validErrorDom(getValidId().add_nation, getErrorMsgId().add_nation, data.msg);
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

        $(getParamId().updateNation).blur(function () {
            var nation = $(getParamId().updateNation).val();
            if (nation.length <= 0 || nation.length > 30) {
                validErrorDom(getValidId().update_nation, getErrorMsgId().update_nation, '民族30个字符以内');
            } else {
                // 民族是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + getAjaxUrl().update_valid,
                    type: 'post',
                    data: {nationId:$(getParamId().updateNationId).val(),nationName:nation},
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(getValidId().update_nation, getErrorMsgId().update_nation);
                        } else {
                            validErrorDom(getValidId().update_nation, getErrorMsgId().update_nation, data.msg);
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
        $('#nation_add').click(function () {
            $('#addNationModal').modal('show');
        });

        $('#add_nation').click(function(){
            validSave();
        });

        /*
         编辑页面
         */
        function edit(nationId,nationName) {
            $(getParamId().updateNationId).val(nationId);
            $(getParamId().updateNation).val(nationName);
            $('#updateNationModal').modal('show');
        }

        $('#update_nation').click(function(){
            validUpdate();
        });

        function validSave(){
            var nation = $(getParamId().addNation).val();
            if (nation.length <= 0 || nation.length > 30) {
                validErrorDom(getValidId().add_nation, getErrorMsgId().add_nation, '民族30个字符以内');
            } else {
                $.post(web_path + getAjaxUrl().save_valid,$('#add_form').serialize(),function(data){
                    if (data.state) {
                        validSuccessDom(getValidId().add_nation, getErrorMsgId().add_nation);
                        sendAjax('保存',web_path + getAjaxUrl().save,'#add_form');
                    } else {
                        validErrorDom(getValidId().add_nation, getErrorMsgId().add_nation, data.msg);
                    }
                });
            }
        }

        function validUpdate(){
            var nation = $(getParamId().updateNation).val();
            if (nation.length <= 0 || nation.length > 30) {
                validErrorDom(getValidId().update_nation, getErrorMsgId().update_nation, '民族30个字符以内');
            } else {
                $.post(web_path + getAjaxUrl().update_valid,{nationId:$(getParamId().updateNationId).val(),nationName:nation},function(data){
                    if (data.state) {
                        validSuccessDom(getValidId().update_nation, getErrorMsgId().update_nation);
                        sendAjax('更新',web_path + getAjaxUrl().update,'#update_form');
                    } else {
                        validErrorDom(getValidId().update_nation, getErrorMsgId().update_nation, data.msg);
                    }
                });
            }
        }

        function sendAjax(message,url,id){
            Messenger().run({
                successMessage: message + '民族成功',
                errorMessage: message + '民族失败',
                progressMessage: '正在' + message + '民族....'
            }, {
                url: url,
                type: 'post',
                data: $(id).serialize(),
                success: function (data) {
                    if (data.state) {
                        myTable.ajax.reload();
                        validCleanDom(getValidId().add_nation, getErrorMsgId().add_nation);
                        validCleanDom(getValidId().update_nation, getErrorMsgId().update_nation);
                        $('#addNationModal').modal('hide');
                        $('#updateNationModal').modal('hide');
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