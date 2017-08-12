package top.zbeboy.isy.web.bean.graduate.design.pharmtech;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor;

/**
 * Created by zbeboy on 2017/7/6.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignHopeTutorBean extends GraduationDesignHopeTutor {
    private String realName;
    private String mobile;
}
