package top.zbeboy.isy.web.bean.graduate.design.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPlan;

/**
 * Created by zbeboy on 2017/6/1.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class GraduationDesignPlanBean extends GraduationDesignPlan {
    private String buildingName;
    private String buildingCode;
    private int buildingId;
}
