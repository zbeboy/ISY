package top.zbeboy.isy.elastic.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-03-27.
 */
@Document(indexName = "systemlog", type = "systemlog", shards = 1, replicas = 0, refreshInterval = "-1")
public class SystemLogElastic {
    @Id
    private String systemLogId;
    private String behavior;
    private Timestamp operatingTime;
    private String username;
    private String ipAddress;

    public SystemLogElastic() {
    }

    public SystemLogElastic(String systemLogId, String behavior, Timestamp operatingTime, String username, String ipAddress) {
        this.systemLogId = systemLogId;
        this.behavior = behavior;
        this.operatingTime = operatingTime;
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public String getSystemLogId() {
        return systemLogId;
    }

    public void setSystemLogId(String systemLogId) {
        this.systemLogId = systemLogId;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public Timestamp getOperatingTime() {
        return operatingTime;
    }

    public void setOperatingTime(Timestamp operatingTime) {
        this.operatingTime = operatingTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "SystemLogElastic{" +
                "systemLogId='" + systemLogId + '\'' +
                ", behavior='" + behavior + '\'' +
                ", operatingTime=" + operatingTime +
                ", username='" + username + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
