plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra
val cloth_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-fabric:${moonlight_version}")

    // mirrored from common
    modCompileOnly("curse.maven:quark-243121:5378961")
    modCompileOnly("curse.maven:zeta-968868:5597406")

    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${cloth_version}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation("curse.maven:yacl-667299:4574163")

    modCompileOnly("curse.maven:modmenu-308702:3920481") {
        exclude(module = "fabric-api")
    }
}
