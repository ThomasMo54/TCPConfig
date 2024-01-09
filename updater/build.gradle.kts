plugins {
    id("java")
    kotlin("jvm") version "1.9.21"
    id("application")
    id("edu.sc.seis.launch4j").version("3.0.4")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

application {
    mainModule = "com.motompro.tcpconfig.updater"
    mainClass = "com.motompro.tcpconfig.updater.UpdaterApp"
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

launch4j {
    outfile = "updater.exe"
    bundledJrePath = "jre"
    jreMinVersion = "17"
    mainClassName = "com.motompro.tcpconfig.updater.UpdaterAppKt"
    productName = "updater"
    setJarTask(project.tasks["jar"])
}
