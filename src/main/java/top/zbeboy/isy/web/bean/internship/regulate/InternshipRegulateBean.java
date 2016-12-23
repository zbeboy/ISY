package top.zbeboy.isy.web.bean.internship.regulate;

import top.zbeboy.isy.domain.tables.pojos.InternshipRegulate;

/**
 * Created by zbeboy on 2016/12/23.
 */
public class InternshipRegulateBean extends InternshipRegulate {
    private String createDateStr;

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }
}
