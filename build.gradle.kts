plugins {
    `java-library`

    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.1"
}

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

group = "me.hapyl"
version = "1.0.0-SNAPSHOT"
description = "A lockout bingo plugin for Minecraft."

tasks {
    bukkitPluginYaml {
        main = "me.hapyl.lockout.Main"
        prefix = "Lockout"
        apiVersion = "26.3"
        authors = listOf("hapyl")
    }

    runServer {
        minecraftVersion("26.3")
    }

}
