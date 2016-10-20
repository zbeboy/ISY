package top.zbeboy.isy.service;


import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Authorities;
import top.zbeboy.isy.domain.tables.pojos.College;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;

import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.tables.Authorities.AUTHORITIES;

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
                .set(AUTHORITIES.USERNAME,authorities.getUsername())
                .set(AUTHORITIES.AUTHORITY,authorities.getAuthority())
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

    @Override
    public boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(authority));
            }
        }
        return false;
    }

    @Override
    public int getRoleCollegeId(Optional<Record> record) {
        int collegeId = 0;
        if(isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            if(record.isPresent()){
                College college = record.get().into(College.class);
                if(!ObjectUtils.isEmpty(college)){
                    collegeId = college.getCollegeId();
                }
            }
        }
        return collegeId;
    }
}
