plugins {
    id("java")
    kotlin("jvm") version "1.9.21"
    id("application")
    id("org.javamodularity.moduleplugin").version("1.8.12")
    id("org.openjfx.javafxplugin").version("0.0.13")
    id("edu.sc.seis.launch4j").version("3.0.4")
}

group = "com.motompro.tcpconfig"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.yaml:snakeyaml:2.2")
}

application {
    mainModule = "com.motompro.tcpconfig.app"
    mainClass = "com.motompro.tcpconfig.app.TCPConfigApp"
}

kotlin {
    jvmToolchain(17)
}

javafx {
    version = "17.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

launch4j {
    outfile = "TCPConfig.exe"
    bundledJrePath = "jre"
    jreMinVersion = "17"
    mainClassName = "com.motompro.tcpconfig.app.TCPConfigAppKt"
    productName = "TCPConfig"
    icon = "${projectDir}/icon/app-icon.ico"
    manifest = "${projectDir}/TCPConfig.manifest"
    setJarTask(project.tasks["jar"])
}
