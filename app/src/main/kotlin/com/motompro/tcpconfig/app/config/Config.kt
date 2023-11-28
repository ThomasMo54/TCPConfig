package com.motompro.tcpconfig.app.config

data class Config(
    var name: String = "",
    var networkAdapter: String = "",
    var ip: String = "",
    var subnetMask: String = "",
    var defaultGateway: String? = null,
    var preferredDNS: String? = null,
    var auxDNS: String? = null,
)
