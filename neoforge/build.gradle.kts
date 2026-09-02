plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    // mirrored from common (neoforge quark variant)
    modCompileOnly("curse.maven:quark-243121:5594847")
    modCompileOnly("curse.maven:zeta-968868:5597406")

    modCompileOnly("curse.maven:jei-238222:3928388")
    modCompileOnly("curse.maven:configured-457570:5180900")
    modCompileOnly("curse.maven:emi-580555:5704405")
}
