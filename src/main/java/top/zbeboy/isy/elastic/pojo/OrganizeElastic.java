package top.zbeboy.isy.elastic.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by lenovo on 2017-04-09.
 */
@Document(indexName = "organize", type = "organize", shards = 1, replicas = 0, refreshInterval = "-1")
@NoArgsConstructor
@ToString
public class OrganizeElastic {
    @Id
    private String organizeId;
    @Getter
    @Setter
    private String organizeName;
    @Getter
    @Setter
    private Byte organizeIsDel;
    @Getter
    @Setter
    private Integer scienceId;
    @Getter
    @Setter
    private String grade;
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
    private String scienceName;

    public Integer getOrganizeId() {
        return NumberUtils.toInt(organizeId);
    }

    public void setOrganizeId(Integer organizeId) {
        this.organizeId = organizeId + "";
    }
}
