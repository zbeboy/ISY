package top.zbeboy.isy.service;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.daos.InternshipJournalDao;
import top.zbeboy.isy.domain.tables.daos.InternshipReleaseDao;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.INTERNSHIP_JOURNAL;

/**
 * Created by zbeboy on 2016/12/14.
 */
@Service("internshipJournalService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InternshipJournalServiceImpl implements InternshipJournalService {

    private final Logger log = LoggerFactory.getLogger(InternshipJournalServiceImpl.class);

    private final DSLContext create;

    private InternshipJournalDao internshipJournalDao;

    @Autowired
    public InternshipJournalServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.internshipJournalDao = new InternshipJournalDao(configuration);
    }

    @Override
    public InternshipJournal findById(String id) {
        return internshipJournalDao.findById(id);
    }

    @Override
    public List<InternshipJournal> findInIds(String ids) {
        return internshipJournalDao.fetchByInternshipJournalId(ids);
    }

    @Override
    public void batchDelete(List<String> ids) {
        create.deleteFrom(INTERNSHIP_JOURNAL)
                .where(INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID.in(ids))
                .execute();
    }
}
