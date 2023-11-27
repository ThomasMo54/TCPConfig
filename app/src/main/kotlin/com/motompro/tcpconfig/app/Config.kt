package com.motompro.tcpconfig.app

data class Config(
    val name: String,
    val networkAdapter: String,
    val ip: String,
    val subnetMask: String,
    val defaultGateway: String?,
    val preferredDNS: String?,
    val auxDNS: String?,
)
