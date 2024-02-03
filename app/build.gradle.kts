plugins {
    id("java")
    kotlin("jvm") version "1.9.21"
    id("application")
    id("org.javamodularity.moduleplugin").version("1.8.12")
    id("org.openjfx.javafxplugin").version("0.0.13")
    id("edu.sc.seis.launch4j").version("3.0.4")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.yaml:snakeyaml:2.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation(files("libs/dhcp4java.jar"))
}

application {
    mainModule = "com.motompro.tcpconfig.app"
    mainClass = "com.motompro.tcpconfig.app.TCPConfigApp"
}

tasks {
    withType<ProcessResources> {
        val versionFile = File(rootDir, "version.txt")
        versionFile.delete()
        versionFile.createNewFile()
        versionFile.writeText("${rootProject.version}")
        from(versionFile)
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
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
