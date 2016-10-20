package top.zbeboy.isy.service.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 文件名生成工具类
 *
 * @author zbeboy
 * @version 1.0
 * @since 1.0
 */

public class IPTimeStamp {

    private final Logger log = LoggerFactory.getLogger(IPTimeStamp.class);

    private SimpleDateFormat sdf = null;

    private String ip = null;

    public IPTimeStamp() {

    }

    public IPTimeStamp(String ip) {
        this.ip = ip;
    }

    public String getIPTimeRand() {
        StringBuffer buf = new StringBuffer();
        if (this.ip != null) {
            String s[] = this.ip.split("\\.");
            for (int i = 0; i < s.length; i++) {
                buf.append(this.addZero(s[i], 3));
            }
        }
        buf.append(this.getTimeStamp());
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            buf.append(r.nextInt(10));
        }
        return buf.toString();
    }

    private String addZero(String str, int len) {
        StringBuffer s = new StringBuffer();
        s.append(str);
        while (s.length() < len) {
            s.insert(0, "0");
        }
        return s.toString();
    }

    public String getDate() {
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return this.sdf.format(new Date());
    }

    public String getTimeStamp() {
        this.sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return this.sdf.format(new Date());
    }
}
