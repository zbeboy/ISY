package top.zbeboy.isy.service;


import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.zbeboy.isy.domain.tables.pojos.Authorities;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;

import java.util.List;

import static top.zbeboy.isy.domain.Tables.AUTHORITIES;

/**
 * Created by lenovo on 2016-02-21.
 */
@Service("authoritiesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AuthoritiesServiceImpl implements AuthoritiesService {

    private final Logger log = LoggerFactory.getLogger(AuthoritiesServiceImpl.class);

    private final DSLContext create;

    @Autowired
    public AuthoritiesServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<AuthoritiesRecord> findByUsername(String username) {
        return create.selectFrom(AUTHORITIES).where(AUTHORITIES.USERNAME.eq(username)).fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Authorities authorities) {
        create.insertInto(AUTHORITIES)
                .set(AUTHORITIES.USERNAME, authorities.getUsername())
                .set(AUTHORITIES.AUTHORITY, authorities.getAuthority())
                .execute();
    }

    @Override
    public void deleteByUsername(String username) {
        create.deleteFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(username))
                .execute();
    }

    @Override
    public void deleteByAuthorities(String authorities) {
        create.deleteFrom(AUTHORITIES)
                .where(AUTHORITIES.AUTHORITY.eq(authorities))
                .execute();
    }
}
