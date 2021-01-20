package com.occ.occpingtester.Entities

import com.google.type.DateTime

data class FirebasePingMessage(
        var IsSuccess: Boolean = false,
        var PingIdentifier: String? = "",
        var SessionIdentifier: String? = "",
        var StartTime: String? = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "IsSuccess" to IsSuccess,
                "PingIdentifier" to PingIdentifier,
                "SessionIdentifier" to SessionIdentifier,
                "StartTime" to StartTime,
        )
    }
}