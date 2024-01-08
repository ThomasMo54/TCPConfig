import com.ullink.Msbuild

plugins {
    id("com.ullink.msbuild").version("3.15")
}

tasks.register<Msbuild>("buildSolution") {
    solutionFile = "NetInterfaceManager.sln"
    projectFile = file("NetInterfaceManager/NetInterfaceManager.csproj")
    targets = listOf("Clean", "Rebuild")
    destinationDir = "build/msbuild/bin"
}
