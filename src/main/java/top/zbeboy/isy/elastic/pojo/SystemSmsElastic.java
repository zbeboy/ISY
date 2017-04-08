package top.zbeboy.isy.elastic.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-04-08.
 */
@Document(indexName = "systemsms", type = "systemsms", shards = 1, replicas = 0, refreshInterval = "-1")
public class SystemSmsElastic {
    @Id
    private String systemSmsId;
    private Timestamp sendTime;
    private String acceptPhone;
    private String sendCondition;

    public SystemSmsElastic() {
    }

    public SystemSmsElastic(String systemSmsId, Timestamp sendTime, String acceptPhone, String sendCondition) {
        this.systemSmsId = systemSmsId;
        this.sendTime = sendTime;
        this.acceptPhone = acceptPhone;
        this.sendCondition = sendCondition;
    }

    public String getSystemSmsId() {
        return systemSmsId;
    }

    public void setSystemSmsId(String systemSmsId) {
        this.systemSmsId = systemSmsId;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public String getAcceptPhone() {
        return acceptPhone;
    }

    public void setAcceptPhone(String acceptPhone) {
        this.acceptPhone = acceptPhone;
    }

    public String getSendCondition() {
        return sendCondition;
    }

    public void setSendCondition(String sendCondition) {
        this.sendCondition = sendCondition;
    }

    @Override
    public String toString() {
        return "SystemSmsElastic{" +
                "systemSmsId='" + systemSmsId + '\'' +
                ", sendTime=" + sendTime +
                ", acceptPhone='" + acceptPhone + '\'' +
                ", sendCondition='" + sendCondition + '\'' +
                '}';
    }
}
