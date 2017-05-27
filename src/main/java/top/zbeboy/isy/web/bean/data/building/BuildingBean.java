package top.zbeboy.isy.web.bean.data.building;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.Building;

/**
 * Created by zbeboy on 2017/5/27.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class BuildingBean extends Building{
    private String schoolName;
    private String collegeName;
}
