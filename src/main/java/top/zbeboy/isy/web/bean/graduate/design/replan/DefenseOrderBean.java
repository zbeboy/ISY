package top.zbeboy.isy.web.bean.graduate.design.replan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.DefenseOrder;

/**
 * Created by zbeboy on 2017/7/19.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DefenseOrderBean extends DefenseOrder{
    private String scoreTypeName;
    private String graduationDesignReleaseId;
    private String secretaryId;
}
