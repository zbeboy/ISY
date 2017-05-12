package top.zbeboy.isy.web.bean.internship.regulate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.InternshipRegulate;

/**
 * Created by zbeboy on 2016/12/23.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class InternshipRegulateBean extends InternshipRegulate {
    private String createDateStr;
}
