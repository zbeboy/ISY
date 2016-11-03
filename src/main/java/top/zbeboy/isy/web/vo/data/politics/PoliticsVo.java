package top.zbeboy.isy.web.vo.data.politics;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by lenovo on 2016-11-03.
 */
public class PoliticsVo {
    private Integer politicalLandscapeId;
    @NotNull
    @Size(max = 30)
    private String  politicalLandscapeName;

    public Integer getPoliticalLandscapeId() {
        return politicalLandscapeId;
    }

    public void setPoliticalLandscapeId(Integer politicalLandscapeId) {
        this.politicalLandscapeId = politicalLandscapeId;
    }

    public String getPoliticalLandscapeName() {
        return politicalLandscapeName;
    }

    public void setPoliticalLandscapeName(String politicalLandscapeName) {
        this.politicalLandscapeName = politicalLandscapeName;
    }
}
