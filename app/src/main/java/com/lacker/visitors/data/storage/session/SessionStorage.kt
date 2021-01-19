package com.lacker.visitors.data.storage.session

interface SessionStorage {

    var session: Session?

    fun hasSession() = session != null

    fun clearSession() {
        session = null
    }

    // 86400000 millis = 24h
    fun canJustCloseSession(): Boolean = session?.startedMoreThanMillisBefore(86400000) ?: false

    fun shouldNoticeUserHeCanJustCloseSession(): Boolean = canJustCloseSession()

}