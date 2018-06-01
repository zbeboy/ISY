package top.zbeboy.isy.elastic.pojo

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.sql.Date
import java.sql.Timestamp

/**
 * Created by zbeboy 2017-11-19 .
 **/
@Document(indexName = "users", type = "users", shards = 1, replicas = 0, refreshInterval = "-1")
open class UsersElastic {
    @Id
    var username: String? = null
    var password: String? = null
    var enabled: Byte? = null
    var usersTypeId: Int? = null
    var usersTypeName: String? = null
    var realName: String? = null
    var mobile: String? = null
    var avatar: String? = null
    var verifyMailbox: Byte? = null
    var mailboxVerifyCode: String? = null
    var passwordResetKey: String? = null
    var mailboxVerifyValid: Timestamp? = null
    var passwordResetKeyValid: Timestamp? = null
    var langKey: String? = null
    var joinDate: Date? = null
    /**
     * 详见：ElasticBook
     */
    var authorities: Int? = null
    /**
     * 以空格分隔的角色名
     */
    var roleName: String? = null

    constructor()

    constructor(username: String?, password: String?, enabled: Byte?, usersTypeId: Int?, usersTypeName: String?, realName: String?, mobile: String?, avatar: String?, verifyMailbox: Byte?, mailboxVerifyCode: String?, passwordResetKey: String?, mailboxVerifyValid: Timestamp?, passwordResetKeyValid: Timestamp?, langKey: String?, joinDate: Date?, authorities: Int?, roleName: String?) {
        this.username = username
        this.password = password
        this.enabled = enabled
        this.usersTypeId = usersTypeId
        this.usersTypeName = usersTypeName
        this.realName = realName
        this.mobile = mobile
        this.avatar = avatar
        this.verifyMailbox = verifyMailbox
        this.mailboxVerifyCode = mailboxVerifyCode
        this.passwordResetKey = passwordResetKey
        this.mailboxVerifyValid = mailboxVerifyValid
        this.passwordResetKeyValid = passwordResetKeyValid
        this.langKey = langKey
        this.joinDate = joinDate
        this.authorities = authorities
        this.roleName = roleName
    }

}