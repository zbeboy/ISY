package top.zbeboy.isy.web.bean.graduate.design.reorder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.DefenseRate;

/**
 * Created by zbeboy on 2017/7/31.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DefenseRateBean extends DefenseRate{
    private String realName;
}
