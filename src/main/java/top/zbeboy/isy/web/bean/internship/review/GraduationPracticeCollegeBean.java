package top.zbeboy.isy.web.bean.internship.review;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCollege;

/**
 * Created by zbeboy on 2016/12/9.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationPracticeCollegeBean extends GraduationPracticeCollege {
    private String realName;
    private String studentNumber;
    private String organizeName;
}
