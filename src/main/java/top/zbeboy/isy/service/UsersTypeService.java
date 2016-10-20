package top.zbeboy.isy.service;

import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.UsersType;
import top.zbeboy.isy.domain.tables.records.UsersTypeRecord;

/**
 * Created by lenovo on 2016-08-24.
 */
public interface UsersTypeService {

    /**
     * 根据用户类型查询id
     *
     * @param usersTypeName
     * @return
     */
    UsersType findByUsersTypeName(String usersTypeName);

    /**
     * 根据用户id查询类型
     * @param usersTypeId
     * @return
     */
    UsersType findByUsersTypeId(int usersTypeId);

    /**
     * 查询全部
     * @return
     */
    Result<UsersTypeRecord> findAll();
}
