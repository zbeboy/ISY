/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.GraduateArchives;
import top.zbeboy.isy.domain.tables.records.GraduateArchivesRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class GraduateArchivesDao extends DAOImpl<GraduateArchivesRecord, top.zbeboy.isy.domain.tables.pojos.GraduateArchives, String> {

    /**
     * Create a new GraduateArchivesDao without any configuration
     */
    public GraduateArchivesDao() {
        super(GraduateArchives.GRADUATE_ARCHIVES, top.zbeboy.isy.domain.tables.pojos.GraduateArchives.class);
    }

    /**
     * Create a new GraduateArchivesDao with an attached configuration
     */
    @Autowired
    public GraduateArchivesDao(Configuration configuration) {
        super(GraduateArchives.GRADUATE_ARCHIVES, top.zbeboy.isy.domain.tables.pojos.GraduateArchives.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.GraduateArchives object) {
        return object.getGraduateArchivesId();
    }

    /**
     * Fetch records that have <code>graduate_archives_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduateArchives> fetchByGraduateArchivesId(String... values) {
        return fetch(GraduateArchives.GRADUATE_ARCHIVES.GRADUATE_ARCHIVES_ID, values);
    }

    /**
     * Fetch a unique record that has <code>graduate_archives_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.GraduateArchives fetchOneByGraduateArchivesId(String value) {
        return fetchOne(GraduateArchives.GRADUATE_ARCHIVES.GRADUATE_ARCHIVES_ID, value);
    }

    /**
     * Fetch records that have <code>graduate_bill_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduateArchives> fetchByGraduateBillId(String... values) {
        return fetch(GraduateArchives.GRADUATE_ARCHIVES.GRADUATE_BILL_ID, values);
    }

    /**
     * Fetch records that have <code>is_excellent IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduateArchives> fetchByIsExcellent(Byte... values) {
        return fetch(GraduateArchives.GRADUATE_ARCHIVES.IS_EXCELLENT, values);
    }

    /**
     * Fetch records that have <code>archive_number IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduateArchives> fetchByArchiveNumber(String... values) {
        return fetch(GraduateArchives.GRADUATE_ARCHIVES.ARCHIVE_NUMBER, values);
    }

    /**
     * Fetch records that have <code>note IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.GraduateArchives> fetchByNote(String... values) {
        return fetch(GraduateArchives.GRADUATE_ARCHIVES.NOTE, values);
    }
}
