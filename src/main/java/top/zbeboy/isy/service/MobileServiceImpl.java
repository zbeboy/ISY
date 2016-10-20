package top.zbeboy.isy.service;


import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.domain.tables.pojos.SystemSms;
import top.zbeboy.isy.service.util.UUIDUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;

/**
 * Created by lenovo on 2016-05-17.
 */
@Service("mobileService")
public class MobileServiceImpl implements MobileService {

    private final Logger log = LoggerFactory.getLogger(MobileServiceImpl.class);

    @Autowired
    private ISYProperties isyProperties;

    @Resource
    private SystemSmsService systemSmsService;

    @Async
    @Override
    public void sendShortMessage(String mobile, String content) {
        String result = null;
        try {
            String httpUrl = "http://apis.baidu.com/kingtto_media/106sms/106sms";
            content = URLEncoder.encode(content, CharEncoding.UTF_8);
            log.debug(" mobile content : {}", content);
            String httpArg = "mobile=" + mobile + "&content=" + content;
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer();
            httpUrl = httpUrl + "?" + httpArg;
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", isyProperties.getMobile().getApikey());
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, CharEncoding.UTF_8));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug(" mobile result : {}", result);
        SystemSms systemSms = new SystemSms(UUIDUtils.getUUID(),new Timestamp(System.currentTimeMillis()),mobile);
        systemSmsService.save(systemSms);
    }

    @Async
    @Override
    public void sendValidMobileShortMessage(String mobile, String verificationCode) {
        log.debug(" mobile valid : {} : {}", mobile, verificationCode);
        if (isyProperties.getMobile().isOpen()) {
            String content = "【ISY信息平台】 您的验证码:" + verificationCode;
            sendShortMessage(mobile, content);
        } else {
            log.debug(" 管理员已关闭短信发送 ");
        }
    }
}
