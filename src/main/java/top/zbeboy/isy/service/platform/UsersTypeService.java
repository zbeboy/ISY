package top.zbeboy.isy.service.platform;

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
     * @param usersTypeName 用户类型名
     * @return 用户类型
     */
    UsersType findByUsersTypeName(String usersTypeName);

    /**
     * 根据用户id查询类型
     *
     * @param usersTypeId 用户类型id
     * @return 用户类型
     */
    UsersType findByUsersTypeId(int usersTypeId);

    /**
     * 查询全部
     *
     * @return 用户类型
     */
    Result<UsersTypeRecord> findAll();

    /**
     * 当前用户类型
     *
     * @param usersTypeName 用户类型名
     * @return true or false
     */
    boolean isCurrentUsersTypeName(String usersTypeName);
}
