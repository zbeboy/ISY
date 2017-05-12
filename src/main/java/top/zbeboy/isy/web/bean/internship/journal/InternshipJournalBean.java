package top.zbeboy.isy.web.bean.internship.journal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;

/**
 * Created by lenovo on 2016-12-18.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class InternshipJournalBean extends InternshipJournal {
    private String createDateStr;
}
