package top.zbeboy.isy.elastic.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by lenovo on 2017-04-10.
 */
@Document(indexName = "users", type = "users", shards = 1, replicas = 0, refreshInterval = "-1")
@Data
public class UsersElastic {
    @Id
    private String username;
    private String password;
    private Byte enabled;
    private Integer usersTypeId;
    private String usersTypeName;
    private String realName;
    private String mobile;
    private String avatar;
    private Byte verifyMailbox;
    private String mailboxVerifyCode;
    private String passwordResetKey;
    private Timestamp mailboxVerifyValid;
    private Timestamp passwordResetKeyValid;
    private String langKey;
    private Date joinDate;
    /**
     * 详见：ElasticBook
     */
    private Integer authorities;
    /**
     * 以空格分隔的角色名
     */
    private String roleName;
}
