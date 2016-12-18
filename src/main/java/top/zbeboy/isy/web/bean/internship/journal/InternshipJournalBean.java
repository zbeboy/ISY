package top.zbeboy.isy.web.bean.internship.journal;

import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;

/**
 * Created by lenovo on 2016-12-18.
 */
public class InternshipJournalBean extends InternshipJournal {
    private String createDateStr;

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }
}
