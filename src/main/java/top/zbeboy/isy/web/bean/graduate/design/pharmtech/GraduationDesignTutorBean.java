package top.zbeboy.isy.web.bean.graduate.design.pharmtech;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor;

/**
 * Created by lenovo on 2017-05-20.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignTutorBean extends GraduationDesignTutor {
    private String realName;
    private String mobile;
}
