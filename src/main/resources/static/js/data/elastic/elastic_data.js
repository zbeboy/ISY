/**
 * Created by zhaoyin on 17-8-8.
 */
//# sourceURL=elastic_data.js
require(["jquery", "messenger", "tablesaw"], function ($) {
    /*
     ajax url.
     */
    var ajax_url = {
        sync_organize: '/web/data/elastic/sync/organize',
        sync_users: '/web/data/elastic/sync/users',
        sync_student: '/web/data/elastic/sync/student',
        sync_staff: '/web/data/elastic/sync/staff',
        clean_system_log: '/web/data/elastic/clean/system_log',
        clean_mailbox_log: '/web/data/elastic/clean/mailbox_log',
        clean_sms_log: '/web/data/elastic/clean/sms_log'
    };

    init();

    function init() {
        $('#elasticTable').tablesaw().data("tablesaw").refresh();
    }

    /**
     * 同步班级数据
     */
    $('#syncOrganize').click(function () {
        var msg;
        msg = Messenger().post({
            message: "确定同步班级数据吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax(ajax_url.sync_organize);
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
    });

    /**
     * 同步用户数据
     */
    $('#syncUsers').click(function () {
        var msg;
        msg = Messenger().post({
            message: "确定同步用户数据吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax(ajax_url.sync_users);
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
    });

    /**
     * 同步学生数据
     */
    $('#syncStudent').click(function () {
        var msg;
        msg = Messenger().post({
            message: "确定同步学生数据吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax(ajax_url.sync_student);
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
    });

    /**
     * 同步教职工数据
     */
    $('#syncStaff').click(function () {
        var msg;
        msg = Messenger().post({
            message: "确定同步教职工数据吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax(ajax_url.sync_staff);
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
    });

    /**
     * 清空系统日志
     */
    $('#cleanSystemLog').click(function () {
        var msg;
        msg = Messenger().post({
            message: "确定清空系统日志吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax(ajax_url.clean_system_log);
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
    });

    /**
     * 清空邮件日志
     */
    $('#cleanMailbox').click(function () {
        var msg;
        msg = Messenger().post({
            message: "确定清空邮件日志吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax(ajax_url.clean_mailbox_log);
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
    });

    /**
     * 清空短信日志
     */
    $('#cleanSms').click(function () {
        var msg;
        msg = Messenger().post({
            message: "确定清空短信日志吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        sendAjax(ajax_url.clean_sms_log);
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
    });

    /**
     * 发送ajax
     * @param url
     */
    function sendAjax(url) {
        $.get(web_path + url, function (data) {
            if (data.state) {
                Messenger().post({
                    message: data.msg,
                    type: 'success',
                    showCloseButton: true
                });
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    }
});