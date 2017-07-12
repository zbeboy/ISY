package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static top.zbeboy.isy.domain.Tables.*;
/**
 * Created by zbeboy on 2017/7/12.
 */
@Slf4j
@Service("defenseGroupMemberService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseGroupMemberServiceImpl implements DefenseGroupMemberService {

    private final DSLContext create;

    @Autowired
    public DefenseGroupMemberServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public void deleteByDefenseGroupId(String defenseGroupId) {
        create.deleteFrom(DEFENSE_GROUP_MEMBER)
                .where(DEFENSE_GROUP_MEMBER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .execute();
    }
}
