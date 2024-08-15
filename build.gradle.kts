plugins {
    kotlin("jvm") version "2.0.0"
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.azisaba"
version = "1.0.0"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

application {
    mainClass.set("net.azisaba.itemfinderdiff.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-csv:1.11.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
