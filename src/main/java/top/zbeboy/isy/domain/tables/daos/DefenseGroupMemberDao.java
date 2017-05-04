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

import top.zbeboy.isy.domain.tables.DefenseGroupMember;
import top.zbeboy.isy.domain.tables.records.DefenseGroupMemberRecord;


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
public class DefenseGroupMemberDao extends DAOImpl<DefenseGroupMemberRecord, top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember, String> {

    /**
     * Create a new DefenseGroupMemberDao without any configuration
     */
    public DefenseGroupMemberDao() {
        super(DefenseGroupMember.DEFENSE_GROUP_MEMBER, top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember.class);
    }

    /**
     * Create a new DefenseGroupMemberDao with an attached configuration
     */
    @Autowired
    public DefenseGroupMemberDao(Configuration configuration) {
        super(DefenseGroupMember.DEFENSE_GROUP_MEMBER, top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember object) {
        return object.getGroupMemberId();
    }

    /**
     * Fetch records that have <code>group_member_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember> fetchByGroupMemberId(String... values) {
        return fetch(DefenseGroupMember.DEFENSE_GROUP_MEMBER.GROUP_MEMBER_ID, values);
    }

    /**
     * Fetch a unique record that has <code>group_member_id = value</code>
     */
    public top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember fetchOneByGroupMemberId(String value) {
        return fetchOne(DefenseGroupMember.DEFENSE_GROUP_MEMBER.GROUP_MEMBER_ID, value);
    }

    /**
     * Fetch records that have <code>staff_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember> fetchByStaffId(Integer... values) {
        return fetch(DefenseGroupMember.DEFENSE_GROUP_MEMBER.STAFF_ID, values);
    }

    /**
     * Fetch records that have <code>defense_group_id IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember> fetchByDefenseGroupId(String... values) {
        return fetch(DefenseGroupMember.DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID, values);
    }

    /**
     * Fetch records that have <code>note IN (values)</code>
     */
    public List<top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember> fetchByNote(String... values) {
        return fetch(DefenseGroupMember.DEFENSE_GROUP_MEMBER.NOTE, values);
    }
}
