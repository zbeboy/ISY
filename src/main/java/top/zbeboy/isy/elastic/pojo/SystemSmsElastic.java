package top.zbeboy.isy.elastic.pojo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-04-08.
 */
@Document(indexName = "systemsms", type = "systemsms", shards = 1, replicas = 0, refreshInterval = "-1")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class SystemSmsElastic {
    @Id
    private String systemSmsId;
    private Timestamp sendTime;
    private String acceptPhone;
    private String sendCondition;
}
