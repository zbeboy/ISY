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
        "check.all": web_path + "/plugin/checkall/checkall",
        "datatables.responsive": web_path + "/plugin/datatables/js/datatables.responsive",
        "datatables.net": web_path + "/plugin/datatables/js/jquery.dataTables.min",
        "datatables.bootstrap": web_path + "/plugin/datatables/js/dataTables.bootstrap.min",
        "csrf": web_path + "/js/util/csrf",
        "com": web_path + "/js/util/com",
        "nav": web_path + "/js/util/nav",
        "nav_active": web_path + "/js/util/nav_active",
        "constants": web_path + "/js/util/constants",
        "ajax_loading_view": web_path + "/js/util/ajax_loading_view",
        "jquery.address": web_path + "/plugin/jquery_address/jquery.address-1.6.min",
        "bootstrap-datetimepicker-zh-CN": web_path + "/plugin/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN",
        "bootstrap-datetimepicker": web_path + "/plugin/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min",
        "bootstrap-daterangepicker": web_path + "/plugin/bootstrap-daterangepicker/daterangepicker",
        "bootstrap-select": web_path + "/plugin/bootstrap-select/js/bootstrap-select.min",
        "bootstrap-select-zh-CN": web_path + "/plugin/bootstrap-select/js/i18n/defaults-zh_CN.min",
        "bootstrap-duallistbox": web_path + "/plugin/bootstrap-duallistbox/jquery.bootstrap-duallistbox.min",
        "moment": web_path + "/plugin/moment/moment.min",
        "moment-with-locales": web_path + "/plugin/moment/moment-with-locales",
        "jquery-ui/widget": web_path + "/plugin/jquery_file_upload/js/vendor/jquery.ui.widget",
        "jquery.iframe-transport": web_path + "/plugin/jquery_file_upload/js/jquery.iframe-transport",
        "jquery.fileupload-process": web_path + "/plugin/jquery_file_upload/js/jquery.fileupload-process",
        "jquery.fileupload": web_path + "/plugin/jquery_file_upload/js/jquery.fileupload",
        "jquery.fileupload-validate": web_path + "/plugin/jquery_file_upload/js/jquery.fileupload-validate",
        "jquery.simple-pagination": web_path + "/plugin/jquery_simple_pagination/jquery.simplePagination"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "check.all": {
            deps: ["jquery"]
        },
        "datatables.responsive": {
            deps: ["datatables.bootstrap", "css!" + web_path + "/plugin/datatables/css/dataTables.bootstrap.min",
                "css!" + web_path + "/plugin/datatables/css/datatables.responsive"]
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
            deps: ["css!" + web_path + "/plugin/bootstrap-daterangepicker/daterangepicker"]
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
        "jquery.fileupload-validate": {
            deps: ["jquery.fileupload", "jquery.fileupload-process", "css!" + web_path + "/plugin/jquery_file_upload/css/jquery.fileupload"]
        },
        "jquery.simple-pagination": {
            deps: ["jquery"]
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

require(["jquery", "ajax_loading_view", "requirejs-domready", "handlebars", "sockjs-client", "moment-with-locales", "csrf", "stomp-websocket", "com", "jquery.address", "nav"],
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

            for (var i = 0; i < li.length; i++) {
                $(li[i]).removeClass('active');
            }

            var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
            if (width < 768) {
                $('div.navbar-collapse').collapse('hide');
            }

            var parent = $(obj).addClass('active').parent().parent().addClass('in').parent();
            if (parent.is('li')) {
                parent.addClass('active');
            }

        }

        function getAjaxUrl() {
            return {
                socke_js_url: '/remind',
                stomp_client_sub_url: '/user/topic/reminds',
                stomp_send_url: '/app/remind',
                more_message_url: '/anyone/message',
                message_detail_url: '/anyone/message/detail'
            };
        }

        initRemind();

        /**
         * 初始化导航消息
         */
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
                stompClient.send(getAjaxUrl().stomp_send_url, {}, frame.headers['user-name']);
                window.setInterval(function () {
                    stompClient.send(getAjaxUrl().stomp_send_url, {}, frame.headers['user-name']);
                }, 180000);
            });
        }

        /**
         * 展示数据alert数据
         * @param data
         */
        function showAlerts(data) {
            var count = data.mapResult.alertsCount;
            if (count > 0) {
                var alertsCount = $('#alertsCount');
                alertsCount.removeClass('hidden');
                alertsCount.text(count);
            }
            moment.locale('zh-cn');
            var source = $("#alert-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('date', function () {
                var value = Handlebars.escapeExpression(moment(this.alertDateStr, "YYYYMMDDhmmss").fromNow());
                return new Handlebars.SafeString(value);
            });

            var html = template(data.mapResult);
            var alerts = $('#alerts');
            alerts.html(html);
            alerts.append(lastTagLi('更多提醒', 'more_alert'));
        }

        /**
         * 展示数据messages数据
         * @param data
         */
        function showMessages(data) {
            var count = data.mapResult.messagesCount;
            if (count > 0) {
                var messagesCount = $('#messagesCount');
                messagesCount.removeClass('hidden');
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
                var value = Handlebars.escapeExpression(this.messageContent.substring(0, 50) + "...");
                return new Handlebars.SafeString(value);
            });

            var html = template(data.mapResult);
            var alerts = $('#messages');
            alerts.html(html);
            alerts.append(lastTagLi('更多消息', 'more_message'));
        }

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
            var id = $(this).attr('data-id');
            $.address.value(getAjaxUrl().message_detail_url + '?id=' + id);
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