package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.SystemMessageDao;
import top.zbeboy.isy.domain.tables.pojos.SystemMessage;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.SYSTEM_MESSAGE;
import static top.zbeboy.isy.domain.Tables.USERS;

/**
 * Created by lenovo on 2016-12-24.
 */
@Service("systemMessageService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemMessageServiceImpl implements SystemMessageService {

    private final Logger log = LoggerFactory.getLogger(SystemMessageServiceImpl.class);

    private final DSLContext create;

    @Resource
    private SystemMessageDao systemMessageDao;

    @Autowired
    public SystemMessageServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Optional<Record> findByIdRelation(String id) {
        return create.select()
                .from(SYSTEM_MESSAGE)
                .join(USERS)
                .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                .where(SYSTEM_MESSAGE.SYSTEM_MESSAGE_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Result<Record> findAllByPageForShow(int pageNum, int pageSize, String username, boolean isSee) {
        Byte b = 0;
        if (isSee) {
            b = 1;
        }
        return create.select()
                .from(SYSTEM_MESSAGE)
                .join(USERS)
                .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                .where(SYSTEM_MESSAGE.ACCEPT_USERS.eq(username).and(SYSTEM_MESSAGE.IS_SEE.eq(b)))
                .orderBy(SYSTEM_MESSAGE.MESSAGE_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public int countAllForShow(String username, boolean isSee) {
        Byte b = 0;
        if (isSee) {
            b = 1;
        }
        Record1<Integer> record = create.selectCount()
                .from(SYSTEM_MESSAGE)
                .where(SYSTEM_MESSAGE.ACCEPT_USERS.eq(username).and(SYSTEM_MESSAGE.IS_SEE.eq(b)))
                .fetchOne();
        return record.value1();
    }

    @Override
    public Result<Record> findAllByPage(PaginationUtils paginationUtils, SystemMessageBean systemMessageBean) {
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, systemMessageBean);
        return create.select()
                .from(SYSTEM_MESSAGE)
                .join(USERS)
                .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                .where(a)
                .orderBy(SYSTEM_MESSAGE.MESSAGE_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public List<SystemMessageBean> dealData(PaginationUtils paginationUtils, Result<Record> records, SystemMessageBean systemMessageBean) {
        List<SystemMessageBean> systemMessageBeens = new ArrayList<>();
        if (records.isNotEmpty()) {
            systemMessageBeens = records.into(SystemMessageBean.class);
            systemMessageBeens.forEach(i -> {
                i.setMessageDateStr(DateTimeUtils.formatDate(i.getMessageDate(), "yyyy年MM月dd日 hh:mm:ss"));
            });
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, systemMessageBean));
        }
        return systemMessageBeens;
    }

    @Override
    public int countByCondition(PaginationUtils paginationUtils, SystemMessageBean systemMessageBean) {
        Record1<Integer> count;
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, systemMessageBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(SYSTEM_MESSAGE);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(SYSTEM_MESSAGE)
                    .join(USERS)
                    .on(SYSTEM_MESSAGE.SEND_USERS.eq(USERS.USERNAME))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(SystemMessage systemMessage) {
        systemMessageDao.insert(systemMessage);
    }

    /**
     * 搜索条件
     *
     * @param paginationUtils 分页工具
     * @return 条件
     */
    public Condition searchCondition(PaginationUtils paginationUtils) {
        Condition a = null;
        JSONObject search = JSON.parseObject(paginationUtils.getSearchParams());
        if (!ObjectUtils.isEmpty(search)) {
            String messageTitle = StringUtils.trimWhitespace(search.getString("messageTitle"));
            if (StringUtils.hasLength(messageTitle)) {
                a = SYSTEM_MESSAGE.MESSAGE_TITLE.like(SQLQueryUtils.likeAllParam(messageTitle));
            }
        }
        return a;
    }

    /**
     * 其它条件参数
     *
     * @param a                 搜索条件
     * @param systemMessageBean 额外参数
     * @return 条件
     */
    private Condition otherCondition(Condition a, SystemMessageBean systemMessageBean) {
        if (!ObjectUtils.isEmpty(systemMessageBean)) {
            if (StringUtils.hasLength(systemMessageBean.getAcceptUsers())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(SYSTEM_MESSAGE.ACCEPT_USERS.eq(systemMessageBean.getAcceptUsers()));
                } else {
                    a = SYSTEM_MESSAGE.ACCEPT_USERS.eq(systemMessageBean.getAcceptUsers());
                }
            }
        }
        return a;
    }

}
