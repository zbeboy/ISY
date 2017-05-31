package top.zbeboy.isy.web.vo.data.building;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/5/31.
 */
@Data
public class BuildingVo {
    private Integer buildingId;
    @NotNull
    @Size(max = 200)
    private String buildingName;
    private Byte buildingIsDel;
    private Integer collegeId;
}
