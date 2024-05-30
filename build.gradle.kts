
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val asmVersion = "9.3"
val flatLafVersion = "2.4"
val log4jVersion = "2.18.0"


group = "com.github.wcaleniekubaa"
version = "1.1.0"

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")

}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.ow2.asm:asm:${asmVersion}")
    implementation("org.ow2.asm:asm-commons:${asmVersion}")
    implementation("org.ow2.asm:asm-tree:${asmVersion}")

    implementation("com.formdev:flatlaf:${flatLafVersion}")
    implementation("com.formdev:flatlaf-intellij-themes:${flatLafVersion}")

    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    implementation("com.opencsv:opencsv:5.7.0")
    implementation("org.benf:cfr:0.152")
    implementation(files("libs/procyon.jar"))
    implementation(files("libs/jd-core-1.1.3.jar")) // I have spent hours on implementing support for an outdated decompiler
    implementation("com.github.fesh0r:fernflower:master") // We want newest version, aren't we?


}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "com.github.wcaleniekubaa.fmd.FMDMain"
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}