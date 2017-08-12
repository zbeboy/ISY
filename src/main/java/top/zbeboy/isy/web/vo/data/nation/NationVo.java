package top.zbeboy.isy.web.vo.data.nation;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-11-02.
 */
@Data
public class NationVo {
    private Integer nationId;
    @NotNull
    @Size(max = 30)
    private String  nationName;
}
