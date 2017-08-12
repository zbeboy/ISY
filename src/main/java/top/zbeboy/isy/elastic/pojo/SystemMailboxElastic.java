package top.zbeboy.isy.elastic.pojo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-04-08.
 */
@Document(indexName = "systemmailbox", type = "systemmailbox", shards = 1, replicas = 0, refreshInterval = "-1")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@Data
public class SystemMailboxElastic {
    @Id
    private String systemMailboxId;
    private Timestamp sendTime;
    private String acceptMail;
    private String sendCondition;
}
