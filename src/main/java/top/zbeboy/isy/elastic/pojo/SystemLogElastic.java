package top.zbeboy.isy.elastic.pojo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-03-27.
 */
@Document(indexName = "systemlog", type = "systemlog", shards = 1, replicas = 0, refreshInterval = "-1")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Data
public class SystemLogElastic {
    @Id
    private String systemLogId;
    private String behavior;
    private Timestamp operatingTime;
    private String username;
    private String ipAddress;
}
