package com.motompro.tcpconfig.app.exception

class InvalidConfigFieldException(
    val type: Type,
    val fieldName: String? = null,
) : Exception() {

    enum class Type {
        NAME_TOO_LONG,
        BAD_IP_FORMAT,
        MISSING_MANDATORY_FIELD,
        MISSING_DNS_FIELD,
    }
}
