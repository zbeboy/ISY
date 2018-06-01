package top.zbeboy.isy.config

import org.jooq.Transaction
import org.springframework.transaction.TransactionStatus

/**
 * Created by zbeboy 2017-10-30 .
 **/
class SpringTransaction(val tx: TransactionStatus) : Transaction