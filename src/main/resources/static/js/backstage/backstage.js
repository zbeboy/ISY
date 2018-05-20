/**
 * Created by lenovo on 2016-08-19.
 */
//# sourceURL=backstage.js
requirejs.config({
    map: {
        '*': {
            'css': web_path + '/webjars/require-css/css.min.js' // or whatever the path to require-css is
        }
    },
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "jquery.showLoading": web_path + "/plugin/loading/js/jquery.showLoading.min",
        "check.all": web_path + "/plugin/checkall/checkall.min",
        "datatables.responsive": web_path + "/plugin/datatables/js/datatables.responsive.min",
        "datatables.net": web_path + "/plugin/datatables/js/jquery.dataTables.min",
        "datatables.bootstrap": web_path + "/plugin/datatables/js/dataTables.bootstrap.min",
        "csrf": web_path + "/js/util/csrf",
        "attribute_extensions": web_path + "/js/util/attribute_extensions",
        "nav": web_path + "/js/util/nav",
        "nav_active": web_path + "/js/util/nav_active",
        "files": web_path + "/js/util/files",
        "constants": web_path + "/js/util/constants",
        "ajax_loading_view": web_path + "/js/util/ajax_loading_view",
        "jquery.address": ["https://lib.baomitu.com/jquery.address/1.6/jquery.address.min",
            web_path + "/plugin/jquery_address/jquery.address-1.6.min"],
        "bootstrap-datetimepicker-zh-CN": web_path + "/plugin/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.min",
        "bootstrap-datetimepicker": web_path + "/plugin/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min",
        "bootstrap-daterangepicker": web_path + "/plugin/bootstrap-daterangepicker/daterangepicker.min",
        "clockface": web_path + "/plugin/clockface/js/clockface.min",
        "bootstrap-select": web_path + "/plugin/bootstrap-select/js/bootstrap-select.min",
        "bootstrap-select-zh-CN": web_path + "/plugin/bootstrap-select/js/i18n/defaults-zh_CN.min",
        "bootstrap-duallistbox": web_path + "/plugin/bootstrap-duallistbox/jquery.bootstrap-duallistbox.min",
        "bootstrap-maxlength": ["https://lib.baomitu.com/bootstrap-maxlength/1.7.0/bootstrap-maxlength.min",
            web_path + "/plugin/bootstrap-maxlength/bootstrap-maxlength.min"],
        "moment": ["https://lib.baomitu.com/moment.js/2.13.0/moment.min",
            web_path + "/plugin/moment/moment.min"],
        "moment-with-locales": web_path + "/plugin/moment/moment-with-locales.min",
        "jquery-ui/widget": web_path + "/plugin/jquery_file_upload/js/vendor/jquery.ui.widget.min",
        "jquery.iframe-transport": web_path + "/plugin/jquery_file_upload/js/jquery.iframe-transport.min",
        "jquery.fileupload-process": web_path + "/plugin/jquery_file_upload/js/jquery.fileupload-process.min",
        "jquery.fileupload": web_path + "/plugin/jquery_file_upload/js/jquery.fileupload.min",
        "jquery.fileupload-validate": web_path + "/plugin/jquery_file_upload/js/jquery.fileupload-validate.min",
        "jquery.simple-pagination": web_path + "/plugin/jquery_simple_pagination/jquery.simplePagination.min",
        "quill": web_path + "/plugin/quill/quill.min",
        "jquery.print": web_path + "/plugin/jquery_print/jquery.print.min",
        "jquery.cropper": web_path + "/plugin/jquery_cropper/cropper.min",
        "jquery.cropper.upload": web_path + "/plugin/jquery_cropper/cropper.upload.min",
        "jquery.entropizer": web_path + "/plugin/jquery_entropizer/js/jquery-entropizer.min",
        "jquery.mark": web_path + "/plugin/jquery_mark/jquery.mark.min",
        "entropizer": web_path + "/plugin/jquery_entropizer/js/entropizer.min",
        "icheck": web_path + "/plugin/icheck/icheck.min",
        "tablesaw": web_path + "/plugin/tablesaw/tablesaw.jquery.min",
        "sb-admin": web_path + "/plugin/sb-admin-2/js/sb-admin-2.min",
        "lodash_plugin": web_path + "/js/util/lodash_plugin"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "check.all": {
            deps: ["jquery"]
        },
        "datatables.responsive": {
            deps: ["datatables.bootstrap", "css!" + web_path + "/plugin/datatables/css/dataTables.bootstrap.min",
                "css!" + web_path + "/plugin/datatables/css/datatables.responsive.min"]
        },
        "messenger": {
            deps: ["jquery"]
        },
        "bootstrap-treeview": {
            deps: ["jquery", "css!" + web_path + "/webjars/bootstrap-treeview/bootstrap-treeview.min"]
        },
        "jquery.showLoading": {
            deps: ["jquery"]
        },
        "jquery.address": {
            deps: ["jquery"]
        },
        "bootstrap-datetimepicker-zh-CN": {
            deps: ["bootstrap-datetimepicker", "css!" + web_path + "/plugin/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min"]
        },
        "bootstrap-daterangepicker": {
            deps: ["css!" + web_path + "/plugin/bootstrap-daterangepicker/daterangepicker.min"]
        },
        "clockface": {
            deps: ["css!" + web_path + "/plugin/clockface/css/clockface.min"]
        },
        "bootstrap-select-zh-CN": {
            deps: ["bootstrap-select", "css!" + web_path + "/plugin/bootstrap-select/css/bootstrap-select.min"]
        },
        "bootstrap-duallistbox": {
            deps: ["jquery", "css!" + web_path + "/plugin/bootstrap-duallistbox/bootstrap-duallistbox.min"]
        },
        "jquery-ui/widget": {
            deps: ["jquery"]
        },
        "jquery.iframe-transport": {
            deps: ["jquery"]
        },
        "jquery.fileupload": {
            deps: ["jquery-ui/widget", "jquery.iframe-transport"]
        },
        "jquery.fileupload-process": {
            deps: ["jquery.fileupload"]
        },
        "jquery.fileupload-validate": {
            deps: ["jquery.fileupload-process", "css!" + web_path + "/plugin/jquery_file_upload/css/jquery.fileupload.min"]
        },
        "jquery.simple-pagination": {
            deps: ["jquery"]
        },
        "quill": {
            deps: ["css!" + web_path + "/plugin/quill/quill.bubble.min", "css!" + web_path + "/plugin/quill/quill.snow.min"]
        },
        "jquery.print": {
            deps: ["jquery"]
        },
        "jquery.cropper.upload": {
            deps: ["jquery.cropper", "css!" + web_path + "/plugin/jquery_cropper/cropper.min", "css!" + web_path + "/plugin/jquery_cropper/cropper.upload.min"]
        },
        "jquery.entropizer": {
            deps: ["css!" + web_path + "/plugin/jquery_entropizer/css/jquery-entropizer.min"]
        },
        "jquery.mark": {
            deps: ["jquery"]
        },
        "icheck": {
            deps: ["jquery", "css!" + web_path + "/plugin/icheck/icheck.min"]
        },
        "tablesaw": {
            deps: ["jquery", "css!" + web_path + "/plugin/tablesaw/tablesaw.min"]
        },
        "sb-admin": {
            deps: ["jquery", "metisMenu"]
        }
    }
});

/*
 捕获全局错误
 */
requirejs.onError = function (err) {
    console.log(err.requireType);
    if (err.requireType === 'timeout') {
        console.log('modules: ' + err.requireModules);
    }
    throw err;
};

require(["jquery", "ajax_loading_view", "requirejs-domready", "handlebars", "sockjs-client", "moment-with-locales",
        "csrf", "stomp-websocket", "jquery.address", "nav", "bootstrap-notify"],
    function ($, loadingView, domready, Handlebars, SockJS, moment, csrf) {
        domready(function () {
            //This function is called once the DOM is ready.
            //It will be safe to query the DOM and manipulate
            //DOM nodes in this function.

            /*
             init message.
             */
            Messenger.options = {
                extraClasses: 'messenger-fixed messenger-on-bottom messenger-on-right',
                theme: 'flat'
            };

            /*
             动态链接点击效果
             */
            $('.dy_href').click(function () {
                document.title = $(this).text();
                addActive(this);
            });

            /*
             init jquery address.
             */
            $.address.init(function (event) {
                // 插件初始化,一般这里调用 $('.nav a').address(); 实现链接单击监听
                $('.dy_href').address();
            }).change(function (event) {
                // 当页面地址更改的时候调用,即#号之后的地址更改
                if (event.value !== '/') {
                    loadingView(event.value, '#page-wrapper', web_path);
                }
            });

        });

        /**
         * nav active where page loading.
         */
        function addActive(obj) {
            var id = $('ul.nav a');
            var li = $('ul.nav li');

            for (var i = 0; i < id.length; i++) {
                $(id[i]).removeClass('active').parent().parent().removeClass('in').parent();
            }

            for (var j = 0; j < li.length; j++) {
                $(li[j]).removeClass('active');
            }

            var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
            if (width < 768) {
                $('div.navbar-collapse').collapse('hide');
            }

            var parent = $(obj).addClass('active').parent().parent().addClass('in').parent();
            if (parent.is('li')) {
                parent.addClass('active');
            }

            var thirdParent = parent.parent();
            if (thirdParent.is('ul')) {
                thirdParent.addClass('in');
                thirdParent.parent().addClass('active');
            }
        }

        function getAjaxUrl() {
            return {
                socke_js_url: '/remind',
                stomp_client_sub_url: '/user/topic/reminds',
                stomp_send_url: '/app/remind',
                more_message_url: '/anyone/message',
                more_alert_url: '/anyone/alert',
                message_detail_url: '/anyone/message/detail',
                alert_detail_url: '/anyone/alert/detail'
            };
        }

        initRemind();

        /**
         * 初始化导航消息
         */
        var websocketStomp = null;
        var websocketFrame = null;

        function initRemind() {
            var stompClient = null;
            var socket = new SockJS(web_path + getAjaxUrl().socke_js_url);
            stompClient = Stomp.over(socket);
            var headers = {};
            headers['headerName'] = csrf.token;
            stompClient.connect(headers, function (frame) {
                console.log('Connected: ' + frame);
                stompClient.subscribe(getAjaxUrl().stomp_client_sub_url, function (data) {
                    var json = JSON.parse(data.body);
                    showAlerts(json);// 提醒
                    showMessages(json);// 消息
                });
                websocketStomp = stompClient;
                websocketFrame = frame;
                getRemind();
                window.setInterval(function () {
                    getRemind();
                }, 180000);
            });
        }

        /**
         * 获取提醒数据
         */
        function getRemind() {
            if (websocketStomp) {
                websocketStomp.send(getAjaxUrl().stomp_send_url, {}, '');
            }
        }

        /**
         * 展示数据alert数据
         * @param data
         */
        function showAlerts(data) {
            var count = data.mapResult.alertsCount;
            var alertsCount = $('#alertsCount');
            if (count > 0) {
                alertsCount.removeClass('hidden');
                alertsCount.text(count);
            } else {
                alertsCount.addClass('hidden');
                alertsCount.text(count);
            }
            moment.locale('zh-cn');
            var source = $("#alert-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('date', function () {
                var value = Handlebars.escapeExpression(moment(this.alertDateStr, "YYYYMMDDhmmss").fromNow());
                return new Handlebars.SafeString(value);
            });

            var alerts = $('#alerts');
            alerts.html(template(data.mapResult));
            alerts.append(lastTagLi('更多提醒', 'more_alert'));
        }

        /**
         * 展示数据messages数据
         * @param data
         */
        function showMessages(data) {
            var count = data.mapResult.messagesCount;
            var messagesCount = $('#messagesCount');
            if (count > 0) {
                messagesCount.removeClass('hidden');
                messagesCount.text(count);
            } else {
                messagesCount.addClass('hidden');
                messagesCount.text(count);
            }
            moment.locale('zh-cn');
            var source = $("#message-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('real_name', function () {
                var value = Handlebars.escapeExpression(this.realName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('date', function () {
                var value = Handlebars.escapeExpression(moment(this.messageDateStr, "YYYYMMDDhmmss").fromNow());
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('message_content', function () {
                var content = this.messageContent;
                if (content != null && content > 20) {
                    content = content.substring(0, 20) + "...";
                }
                var value = Handlebars.escapeExpression(content);
                return new Handlebars.SafeString(value);
            });

            var alerts = $('#messages');
            alerts.html(template(data.mapResult));
            alerts.append(lastTagLi('更多消息', 'more_message'));
        }

        /*
         更多提醒
         */
        $('#alerts').delegate('.more_alert', "click", function () {
            $.address.value(getAjaxUrl().more_alert_url);
        });

        /*
         更多消息
         */
        $('#messages').delegate('.more_message', "click", function () {
            $.address.value(getAjaxUrl().more_message_url);
        });

        /*
         消息详情
         */
        $('#wrapper').delegate('.message_detail', "click", function () {
            $.address.value(getAjaxUrl().message_detail_url + '?id=' + $(this).attr('data-id'));
            getRemind();
        });

        /*
         提醒详情
         */
        $('#wrapper').delegate('.alert_detail', "click", function () {
            $.address.value(getAjaxUrl().alert_detail_url + '?id=' + $(this).attr('data-id'));
            getRemind();
        });


        /**
         * 最后的更多html
         * @param msg 文字
         * @param id 识别id
         * @returns {string}
         */
        function lastTagLi(msg, id) {
            return "<li>" +
                "<a class='text-center " + id + "' href='javascript:;'>" +
                "<strong>" + msg + "</strong>" +
                "  <i class='fa fa-angle-right'></i>" +
                "</a>" +
                "</li>";
        }
    });