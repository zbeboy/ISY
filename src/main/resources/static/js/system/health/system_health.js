/**
 * Created by zhaoyin on 17-8-8.
 */
require(["jquery", "jquery.showLoading"],
    function ($) {

        /*
         ajax url.
         */
        var ajax_url = {
            init_data_url: '/health'
        };

        var paramId = {
            allStatus: '#allStatus',
            mailStatus: '#mailStatus',
            mailLocation: '#mailLocation',
            diskStatus: '#diskStatus',
            diskTotal: '#diskTotal',
            diskFree: '#diskFree',
            diskThreshold: '#diskThreshold',
            redisStatus: '#redisStatus',
            redisVersion: '#redisVersion',
            dbStatus: '#dbStatus',
            dbDatabase: '#dbDatabase',
            elasticsearchStatus: '#elasticsearchStatus'
        };

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        init();

        function init() {
            startLoading();
            $.get(web_path + ajax_url.init_data_url, function (data) {
                endLoading();
                outputData(data);
            });
        }

        /**
         * 输出数据
         * @param data
         */
        function outputData(data) {
            dealStatus($(paramId.allStatus),data.status);
            dealStatus($(paramId.mailStatus),data.details.mail.status);
            $(paramId.mailLocation).text(data.details.mail.details.location);
            dealStatus($(paramId.diskStatus),data.details.diskSpace.status);
            $(paramId.diskTotal).text(data.details.diskSpace.details.total);
            $(paramId.diskFree).text(data.details.diskSpace.details.free);
            $(paramId.diskThreshold).text(data.details.diskSpace.details.threshold);
            dealStatus($(paramId.redisStatus),data.details.redis.status);
            $(paramId.redisVersion).text(data.details.redis.details.version);
            dealStatus($(paramId.dbStatus),data.details.db.status);
            $(paramId.dbDatabase).text(data.details.db.details.database);
            dealStatus($(paramId.elasticsearchStatus),data.details.elasticsearch.status);
        }

        /**
         * 处理状态值
         * @param obj
         * @param status
         */
        function dealStatus(obj,status){
            if(status === 'UP'){
                obj.addClass('label').addClass('label-success');
            } else {
                obj.addClass('label').addClass('label-danger');
            }
            obj.text(status);
        }

    });