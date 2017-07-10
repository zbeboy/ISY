package top.zbeboy.isy.web.bean.graduate.design.replan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement;

import java.sql.Date;

/**
 * Created by zbeboy on 2017/7/10.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DefenseArrangementBean extends DefenseArrangement {
    private String paperStartDateStr;
    private String paperEndDateStr;
    private String defenseStartDateStr;
    private String defenseEndDateStr;
}
