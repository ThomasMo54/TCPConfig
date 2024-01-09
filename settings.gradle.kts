plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "tcpconfig"

include("app")
include("net-interface-manager")
include("updater")
