/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain.tables.daos;


import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import top.zbeboy.isy.domain.tables.SystemAlert;
import top.zbeboy.isy.domain.tables.records.SystemAlertRecord;


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
public class SystemAlertDao extends DAOImpl<SystemAlertRecord, top.zbeboy.isy.domain.tables.pojos.SystemAlert, String> {

    /**
     * Create a new SystemAlertDao without any configuration
     */
    public SystemAlertDao() {
        super(SystemAlert.SYSTEM_ALERT, top.zbeboy.isy.domain.tables.pojos.SystemAlert.class);
    }

    /**
     * Create a new SystemAlertDao with an attached configuration
     */
    @Autowired
    public SystemAlertDao(Configuration configuration) {
        super(SystemAlert.SYSTEM_ALERT, top.zbeboy.isy.domain.tables.pojos.SystemAlert.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.SystemAlert object) {
        return object.getSystemAlertId();
    }

    /**
     * Fetch records that have <code>system_alert_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.SystemAlert> fetchBySystemAlertId(String... values) {
        return fetch(SystemAlert.SYSTEM_ALERT.SYSTEM_ALERT_ID, values);
    }

    /**
     * Fetch a unique record that has <code>system_alert_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.SystemAlert fetchOneBySystemAlertId(String value) {
        return fetchOne(SystemAlert.SYSTEM_ALERT.SYSTEM_ALERT_ID, value);
    }

    /**
     * Fetch records that have <code>alert_content IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.SystemAlert> fetchByAlertContent(String... values) {
        return fetch(SystemAlert.SYSTEM_ALERT.ALERT_CONTENT, values);
    }

    /**
     * Fetch records that have <code>alert_date IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.SystemAlert> fetchByAlertDate(Timestamp... values) {
        return fetch(SystemAlert.SYSTEM_ALERT.ALERT_DATE, values);
    }

    /**
     * Fetch records that have <code>link_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.SystemAlert> fetchByLinkId(String... values) {
        return fetch(SystemAlert.SYSTEM_ALERT.LINK_ID, values);
    }

    /**
     * Fetch records that have <code>is_see IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.SystemAlert> fetchByIsSee(Byte... values) {
        return fetch(SystemAlert.SYSTEM_ALERT.IS_SEE, values);
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.SystemAlert> fetchByUsername(String... values) {
        return fetch(SystemAlert.SYSTEM_ALERT.USERNAME, values);
    }

    /**
     * Fetch records that have <code>system_alert_type_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.SystemAlert> fetchBySystemAlertTypeId(Integer... values) {
        return fetch(SystemAlert.SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID, values);
    }
}
