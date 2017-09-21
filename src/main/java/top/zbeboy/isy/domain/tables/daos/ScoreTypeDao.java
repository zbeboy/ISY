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

import top.zbeboy.isy.domain.tables.ScoreType;
import top.zbeboy.isy.domain.tables.records.ScoreTypeRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class ScoreTypeDao extends DAOImpl<ScoreTypeRecord, top.zbeboy.isy.domain.tables.pojos.ScoreType, Integer> {

    /**
     * Create a new ScoreTypeDao without any configuration
     */
    public ScoreTypeDao() {
        super(ScoreType.SCORE_TYPE, top.zbeboy.isy.domain.tables.pojos.ScoreType.class);
    }

    /**
     * Create a new ScoreTypeDao with an attached configuration
     */
    @Autowired
    public ScoreTypeDao(Configuration configuration) {
        super(ScoreType.SCORE_TYPE, top.zbeboy.isy.domain.tables.pojos.ScoreType.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(top.zbeboy.isy.domain.tables.pojos.ScoreType object) {
        return object.getScoreTypeId();
    }

    /**
     * Fetch records that have <code>score_type_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.ScoreType> fetchByScoreTypeId(Integer... values) {
        return fetch(ScoreType.SCORE_TYPE.SCORE_TYPE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>score_type_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.ScoreType fetchOneByScoreTypeId(Integer value) {
        return fetchOne(ScoreType.SCORE_TYPE.SCORE_TYPE_ID, value);
    }

    /**
     * Fetch records that have <code>score_type_name IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.ScoreType> fetchByScoreTypeName(String... values) {
        return fetch(ScoreType.SCORE_TYPE.SCORE_TYPE_NAME, values);
    }
}
