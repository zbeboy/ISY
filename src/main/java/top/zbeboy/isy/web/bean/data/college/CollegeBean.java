package top.zbeboy.isy.web.bean.data.college;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.College;

/**
 * Created by lenovo on 2016-09-21.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class CollegeBean extends College {
    private String schoolName;
}
