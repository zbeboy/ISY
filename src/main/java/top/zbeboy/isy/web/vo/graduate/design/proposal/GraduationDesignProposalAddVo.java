package top.zbeboy.isy.web.vo.graduate.design.proposal;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2017-06-25.
 */
@Data
public class GraduationDesignProposalAddVo {
    @Size(max = 10)
    private String version;
    @NotNull
    private Integer graduationDesignDatumTypeId;
    @NotNull
    @Size(max = 64)
    private String graduationDesignReleaseId;
    private String graduationDesignDatumId;
}
