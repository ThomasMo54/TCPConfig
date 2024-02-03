package com.motompro.tcpconfig.app.exception

class ConfigException(
    val type: Type,
) : Exception() {

    enum class Type {
        NOT_ENOUGH_ARGS,
        INTERFACE_NOT_CONNECTED,
        INTERFACE_NOT_FOUND,
        INTERFACE_NOT_VALID,
    }
}
