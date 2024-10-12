plugins {
    id("fabric-loom") version "1.7.4"
    kotlin("jvm") version "2.0.0"

    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.50"

    id("maven-publish")
}

val minecraft_version: String by project
val loader_version: String by project
val fabric_kotlin_version: String by project
val kotlin_version: String by project
val fabric_version: String by project
val yarn_mappings: String by project
val devauth_version: String by project
val kotlinx_coroutines_version: String by project
val kotlinx_serialization_version: String by project

val versionFile = file("src/main/kotlin/VERSION.kt")
version = versionFile.readText().split("\"")[1]

loom {
    accessWidenerPath = file("src/main/resources/telosaddons.accesswidener")
}

repositories {
    maven(url = "https://repo.essential.gg/repository/maven-public")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")

    maven(url = "https://oss.jfrog.org/simple/libs-snapshot")

    mavenLocal()
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")

    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")


    modImplementation("net.fabricmc:fabric-language-kotlin:${fabric_kotlin_version}")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${devauth_version}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinx_coroutines_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx_serialization_version}")

    modImplementation(include("gg.essential:elementa:670")!!)
    modImplementation(include("gg.essential:vigilance:306")!!)
    modImplementation(include("gg.essential:universalcraft-1.21-fabric:363")!!)
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        filteringCharset = "UTF-8"

        inputs.property("version", version)
        inputs.property("minecraft_version", minecraft_version)
        inputs.property("loader_version", loader_version)

        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to version,
                    "minecraft_version" to minecraft_version,
                    "loader_version" to loader_version,
                    "language_support_version" to "$fabric_kotlin_version+kotlin.$kotlin_version"
                )
            )
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName}" }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "telosaddons"

            from(components["java"])
        }
    }
}