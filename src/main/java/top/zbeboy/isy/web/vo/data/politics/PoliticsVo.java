package top.zbeboy.isy.web.vo.data.politics;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-11-03.
 */
@Data
public class PoliticsVo {
    private Integer politicalLandscapeId;
    @NotNull
    @Size(max = 30)
    private String  politicalLandscapeName;
}
