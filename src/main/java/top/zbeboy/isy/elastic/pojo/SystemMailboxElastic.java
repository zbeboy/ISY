package top.zbeboy.isy.elastic.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-04-08.
 */
@Document(indexName = "systemmailbox", type = "systemmailbox", shards = 1, replicas = 0, refreshInterval = "-1")
public class SystemMailboxElastic {
    @Id
    private String systemMailboxId;
    private Timestamp sendTime;
    private String acceptMail;
    private String sendCondition;

    public SystemMailboxElastic() {
    }

    public SystemMailboxElastic(String systemMailboxId, Timestamp sendTime, String acceptMail, String sendCondition) {
        this.systemMailboxId = systemMailboxId;
        this.sendTime = sendTime;
        this.acceptMail = acceptMail;
        this.sendCondition = sendCondition;
    }

    public String getSystemMailboxId() {
        return systemMailboxId;
    }

    public void setSystemMailboxId(String systemMailboxId) {
        this.systemMailboxId = systemMailboxId;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public String getAcceptMail() {
        return acceptMail;
    }

    public void setAcceptMail(String acceptMail) {
        this.acceptMail = acceptMail;
    }

    public String getSendCondition() {
        return sendCondition;
    }

    public void setSendCondition(String sendCondition) {
        this.sendCondition = sendCondition;
    }

    @Override
    public String toString() {
        return "SystemMailboxElastic{" +
                "systemMailboxId='" + systemMailboxId + '\'' +
                ", sendTime=" + sendTime +
                ", acceptMail='" + acceptMail + '\'' +
                ", sendCondition='" + sendCondition + '\'' +
                '}';
    }
}
