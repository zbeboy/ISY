/**
 * Created by lenovo on 2016-10-19.
 */
require(["jquery", "handlebars", "messenger", "bootstrap-treeview",
        "text!" + web_path + "/webjars/bootstrap-treeview/bootstrap-treeview.min.css",
        "jquery.address"],
    function ($, Handlebars, messenger, treeview, jqueryAddress) {

        /*
         ajax url.
         */
        var ajax_url = {
            application_json_data: '/special/channel/system/application/json',
            college_application_data: '/web/data/college/application/data',
            update: '/web/data/college/update/mount',
            back: '/web/menu/data/college'
        };

        /*
         参数id
         */
        var paramId = {
            collegeId: '#collegeId'
        };

        /*
         参数
         */
        var param = {
            collegeId: $(paramId.collegeId).val(),
            applicationIds: ''
        };

        /**
         * 初始化参数
         */
        function initParam() {
            param.collegeId = $(paramId.collegeId).val();
            param.applicationIds = getAllCheckedData();
        }

        /*
         初始化数据
         */
        init();

        /**
         * 初始化界面
         */
        function init() {
            initTreeView();
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        /*
         保存数据
         */
        $('#save').click(function () {
            add();
        });

        /*
         添加询问
         */
        function add() {
            initParam();
            var msg;
            msg = Messenger().post({
                message: "确定挂载吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            sendAjax();
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
         * 发送数据到后台
         */
        function sendAjax() {
            initParam();
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.update,
                type: 'post',
                data: param,
                success: function (data) {
                    if (data.state) {
                        $.address.value(ajax_url.back);
                    } else {
                        Messenger().post({
                            message: data.msg,
                            type: 'error',
                            showCloseButton: true
                        });
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

        var treeviewId = $('#treeview-checkable');

        /**
         * 初始化tree view
         */
        function initTreeView() {
            $.get(web_path + ajax_url.application_json_data, function (data) {
                if (data.listResult.length > 0) {
                    treeViewData(data.listResult);
                }
            });
        }

        function treeViewData(data) {
            var $checkableTree = treeviewId.treeview({
                data: data,
                showIcon: false,
                showCheckbox: true,
                onNodeChecked: function (event, node) {
                    checkAllParentNode(node);
                    checkAllChildrenNode(node);
                },
                onNodeUnchecked: function (event, node) {
                    uncheckAllChildrenNode(node);
                    getAllParent(node);// 若任何子节点选中则取消选中该父节点
                }
            });

            selectedApplication();
        }

        /**
         * 选中应用
         */
        function selectedApplication() {
            initParam();
            var collegeId = param.collegeId;
            $.post(web_path + ajax_url.college_application_data, {collegeId: collegeId}, function (data) {
                var list = data.listResult;
                if (list.length > 0) {
                    var unCheckeds = treeviewId.treeview('getUnchecked');
                    for (var i = 0; i < list.length; i++) {
                        for (var j = 0; j < unCheckeds.length; j++) {
                            if (list[i].applicationId == unCheckeds[j].dataId) {
                                treeviewId.treeview('checkNode', [unCheckeds[j], {silent: true}]);
                                break;
                            }
                        }
                    }
                }
            });
        }

        /**
         * 选中所有父节点
         * @param node
         */
        function checkAllParentNode(node) {
            if (node.hasOwnProperty('parentId') && node.parentId != undefined) {
                var parentNode = treeviewId.treeview('getParent', node);
                checkAllParentNode(parentNode);
            }
            treeviewId.treeview('checkNode', [node.nodeId, {silent: true}]);
        }

        /**
         * 取消所有子节点的选中
         * @param node
         */
        function uncheckAllChildrenNode(node) {
            if (node.hasOwnProperty('nodes') && node.nodes != null) {
                var n = node.nodes;
                for (var i = 0; i < n.length; i++) {
                    uncheckAllChildrenNode(n[i]);
                }
            }
            treeviewId.treeview('uncheckNode', [node.nodeId, {silent: true}]);
        }

        /**
         * 选中所有子节点
         * @param node
         */
        function checkAllChildrenNode(node) {
            if (node.hasOwnProperty('nodes') && node.nodes != null) {
                var n = node.nodes;
                for (var i = 0; i < n.length; i++) {
                    checkAllChildrenNode(n[i]);
                }
            }
            treeviewId.treeview('checkNode', [node.nodeId, {silent: true}]);
        }

        var childrenArr = [];

        function getAllChildren(node) {
            if (node.hasOwnProperty('nodes') && node.nodes != null) {
                var n = node.nodes;
                for (var i = 0; i < n.length; i++) {
                    getAllChildren(n[i]);
                }
            }
            childrenArr.push(node);
        }

        function getAllParent(node) {
            if (node.hasOwnProperty('parentId') && node.parentId != undefined) {
                var parentNode = treeviewId.treeview('getParent', node);
                childrenArr = [];
                getAllChildren(parentNode);
                var parentNodeIsChecked = false;
                for (var i = 0; i < childrenArr.length; i++) {
                    if (childrenArr[i].nodeId != parentNode.nodeId && childrenArr[i].state.checked) {
                        parentNodeIsChecked = true;
                    }
                }
                if (!parentNodeIsChecked) {
                    treeviewId.treeview('uncheckNode', [parentNode.nodeId, {silent: true}]);
                    getAllParent(parentNode);
                }

            }
        }

        /**
         * 获取所有选中节点的dataId
         * @returns {string}
         */
        function getAllCheckedData() {
            var applicationIds = '';
            var checkeds = treeviewId.treeview('getChecked');
            var temp = [];
            for (var i = 0; i < checkeds.length; i++) {
                temp.push(checkeds[i].dataId);
            }
            if (temp.length > 0) {
                applicationIds = temp.join(",");
            }
            return applicationIds;
        }

    });