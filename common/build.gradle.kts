plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

val moonlight_version: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    modCompileOnly("curse.maven:quark-243121:5378961")
    modCompileOnly("curse.maven:zeta-968868:5597406")
}
