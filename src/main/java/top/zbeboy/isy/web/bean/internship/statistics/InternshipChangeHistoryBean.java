package top.zbeboy.isy.web.bean.internship.statistics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.InternshipChangeHistory;

/**
 * Created by lenovo on 2016-12-12.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class InternshipChangeHistoryBean extends InternshipChangeHistory {
    private String internshipTitle;
    private String realName;
    private String studentNumber;
    private String organizeName;
    private String applyTimeStr;
}
