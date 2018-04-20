/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.InternshipReleaseScience;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseScienceRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class InternshipReleaseScienceDao extends DAOImpl<InternshipReleaseScienceRecord, top.zbeboy.isy.domain.tables.pojos.InternshipReleaseScience, Record2<String, Integer>> {

    /**
     * Create a new InternshipReleaseScienceDao without any configuration
     */
    public InternshipReleaseScienceDao() {
        super(InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE, top.zbeboy.isy.domain.tables.pojos.InternshipReleaseScience.class);
    }

    /**
     * Create a new InternshipReleaseScienceDao with an attached configuration
     */
    @Autowired
    public InternshipReleaseScienceDao(Configuration configuration) {
        super(InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE, top.zbeboy.isy.domain.tables.pojos.InternshipReleaseScience.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Record2<String, Integer> getId(top.zbeboy.isy.domain.tables.pojos.InternshipReleaseScience object) {
        return compositeKeyRecord(object.getInternshipReleaseId(), object.getScienceId());
    }

    /**
     * Fetch records that have <code>internship_release_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipReleaseScience> fetchByInternshipReleaseId(String... values) {
        return fetch(InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID, values);
    }

    /**
     * Fetch records that have <code>science_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.InternshipReleaseScience> fetchByScienceId(Integer... values) {
        return fetch(InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID, values);
    }
}
