package top.zbeboy.isy.web.bean.internship.journal

import top.zbeboy.isy.domain.tables.pojos.InternshipJournal

/**
 * Created by zbeboy 2017-12-29 .
 **/
class InternshipJournalBean : InternshipJournal() {
    var createDateStr: String? = null

    // 小组内统计个人日志数量
    var journalNum: Int? = 0
    var studentRealName: String? = null
    // 用于在service层取别名用
    companion object {
        @JvmField
        val JOURNAL_NUM = "journalNum"
    }
}