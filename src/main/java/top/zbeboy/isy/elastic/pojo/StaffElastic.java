package top.zbeboy.isy.elastic.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Date;

/**
 * Created by lenovo on 2017-04-12.
 */
@Document(indexName = "staff", type = "staff", shards = 1, replicas = 0, refreshInterval = "-1")
@NoArgsConstructor
@ToString
public class StaffElastic {
    @Id
    private String staffId;
    @Getter
    @Setter
    private String staffNumber;
    @Getter
    @Setter
    private Date birthday;
    @Getter
    @Setter
    private String sex;
    @Getter
    @Setter
    private String idCard;
    @Getter
    @Setter
    private String familyResidence;
    @Getter
    @Setter
    private Integer politicalLandscapeId;
    @Getter
    @Setter
    private String politicalLandscapeName;
    @Getter
    @Setter
    private Integer nationId;
    @Getter
    @Setter
    private String nationName;
    @Getter
    @Setter
    private Integer academicTitleId;
    @Getter
    @Setter
    private String academicTitleName;
    @Getter
    @Setter
    private String post;
    @Getter
    @Setter
    private Integer schoolId;
    @Getter
    @Setter
    private String schoolName;
    @Getter
    @Setter
    private Integer collegeId;
    @Getter
    @Setter
    private String collegeName;
    @Getter
    @Setter
    private Integer departmentId;
    @Getter
    @Setter
    private String departmentName;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private Byte enabled;
    @Getter
    @Setter
    private String realName;
    @Getter
    @Setter
    private String mobile;
    @Getter
    @Setter
    private String avatar;
    @Getter
    @Setter
    private String langKey;
    @Getter
    @Setter
    private Date joinDate;
    /**
     * -1 : 无权限
     * 0 :  有权限
     * 1 : 系统
     * 2 : 管理员
     */
    @Getter
    @Setter
    private Integer authorities;
    /**
     * 以空格分隔的角色名
     */
    @Getter
    @Setter
    private String roleName;

    public Integer getStaffId() {
        return NumberUtils.toInt(staffId);
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId + "";
    }
}
