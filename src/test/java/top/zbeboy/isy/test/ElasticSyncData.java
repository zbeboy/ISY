package top.zbeboy.isy.test;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.zbeboy.isy.Application;
import top.zbeboy.isy.elastic.pojo.OrganizeElastic;
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;
import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic;
import top.zbeboy.isy.elastic.pojo.SystemSmsElastic;
import top.zbeboy.isy.elastic.repository.OrganizeElasticRepository;
import top.zbeboy.isy.elastic.repository.SystemLogElasticRepository;
import top.zbeboy.isy.elastic.repository.SystemMailboxElasticRepository;
import top.zbeboy.isy.elastic.repository.SystemSmsElasticRepository;

import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2017-04-09.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class ElasticSyncData {

    @Autowired
    DSLContext create;

    @Autowired
    OrganizeElasticRepository organizeElasticRepository;

    @Autowired
    SystemLogElasticRepository systemLogElasticRepository;

    @Autowired
    SystemMailboxElasticRepository systemMailboxElasticRepository;

    @Autowired
    SystemSmsElasticRepository systemSmsElasticRepository;

    @Test
    public void syncOrganizeData() {
        organizeElasticRepository.deleteAll();
        Result<Record> record = create.select()
                .from(ORGANIZE)
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .fetch();

        if(record.isNotEmpty()){
            List<OrganizeElastic> organizeElastics = record.into(OrganizeElastic.class);
            organizeElastics.forEach(i->organizeElasticRepository.save(i));
        }
    }

    @Test
    public void syncSystemLogData() {
        systemLogElasticRepository.deleteAll();
        Result<Record> record = create.select()
                .from(SYSTEM_LOG)
                .fetch();

        if(record.isNotEmpty()){
            List<SystemLogElastic> systemLogElastics = record.into(SystemLogElastic.class);
            systemLogElastics.forEach(i->systemLogElasticRepository.save(i));
        }
    }

    @Test
    public void syncSystemMailboxData() {
        systemMailboxElasticRepository.deleteAll();
        Result<Record> record = create.select()
                .from(SYSTEM_MAILBOX)
                .fetch();

        if(record.isNotEmpty()){
            List<SystemMailboxElastic> systemMailboxElastics = record.into(SystemMailboxElastic.class);
            systemMailboxElastics.forEach(i->systemMailboxElasticRepository.save(i));
        }
    }

    @Test
    public void syncSystemSmsData() {
        systemSmsElasticRepository.deleteAll();
        Result<Record> record = create.select()
                .from(SYSTEM_SMS)
                .fetch();

        if(record.isNotEmpty()){
            List<SystemSmsElastic> systemSmsElastics = record.into(SystemSmsElastic.class);
            systemSmsElastics.forEach(i->systemSmsElasticRepository.save(i));
        }
    }
}
