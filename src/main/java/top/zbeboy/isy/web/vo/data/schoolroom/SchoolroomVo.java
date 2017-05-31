package top.zbeboy.isy.web.vo.data.schoolroom;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zbeboy on 2017/5/31.
 */
@Data
public class SchoolroomVo {
    private Integer schoolroomId;
    @NotNull
    @Size(max = 10)
    private String buildingCode;
    private Byte schoolroomIsDel;
    @NotNull
    @Min(1)
    private Integer buildingId;
}
