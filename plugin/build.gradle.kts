plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven("https://repo.okaeri.cloud/releases")
    maven("https://repo.panda-lang.org/releases")
    maven("https://repo.eternalcode.pl/releases")
}

dependencies {
    compileOnly(libs.paper.api)

    implementation(libs.guice)

    implementation(libs.litecommands.bukkit)
    implementation(libs.litecommands.adventure)

    implementation(libs.okaeri.configs.yaml.bukkit)

    implementation(libs.multification.paper)
    implementation(libs.multification.okaeri)
}

kotlin {
    jvmToolchain(25)
}

tasks {
    shadowJar {
        archiveFileName.set("arpSignatures-v${project.version}.jar")

        val relocatePrefix = "xyz.arpdev.plugin.libs"

        relocate("com.google.inject", "$relocatePrefix.guice")
        relocate("dev.rollczi.litecommands", "$relocatePrefix.litecommands")
        relocate("eu.okaeri.configs", "$relocatePrefix.configs")
        relocate("com.eternalcode.multification", "$relocatePrefix.multification")

        mergeServiceFiles()
    }
    processResources {
        val props = mapOf(
            "version" to project.version
        )
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
    compileJava {
        options.compilerArgs.add("-parameters")
    }
}
