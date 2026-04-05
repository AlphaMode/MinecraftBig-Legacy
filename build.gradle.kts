plugins {
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT"
    id("ploceus") version "1.15-SNAPSHOT"
}

val mc = if (hasProperty("deps.minecraft"))
    property("deps.minecraft").toString() else stonecutter.current.version

base.archivesName = property("archives_base_name").toString()

version = property("mod_version").toString()
group = property("maven_group").toString()

loom {
    val ctFile = rootProject.file("src/main/resources/mcbig.classtweaker")
    accessWidenerPath = sc.process(ctFile, "build/processed.classtweaker")

    mods {
        register("mcbig") {
            sourceSet(sourceSets.main.get())
        }
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }
}

ploceus {
    setIntermediaryGeneration(2)
    disableLvtPatch() // Lvt patching is still experimental right now and leads to decompiler issues
}

repositories {
    maven("https://libraries.minecraft.net")
    maven("https://mvn.devos.one/releases")
}

val nostaliaVersion = mc + "+build." + property("deps.nostalgia") as String

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$mc")
    mappings(ploceus.layeredMappings {
        mappings("me.alphamode:nostalgia:$nostaliaVersion:v2", {
            containsUnpick()
        })
    })

    implementation(files("libs/nostalgia-b1.7.3+build.51-constants.jar"))

    implementation(include("com.mojang:brigadier:1.3.10")!!)

    modImplementation("net.fabricmc:fabric-loader:${property("deps.loader")}")

    clientExceptions(ploceus.raven(property("deps.client_raven").toString(), "client"))
    serverExceptions(ploceus.raven(property("deps.server_raven").toString(), "server"))

    clientSignatures(ploceus.sparrow(property("deps.client_sparrow").toString(), "client"))
    serverSignatures(ploceus.sparrow(property("deps.server_sparrow").toString(), "server"))

    clientNests(ploceus.nests(property("deps.client_nests").toString(), "client"))
    serverNests(ploceus.nests(property("deps.server_nests").toString(), "server"))

//    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("net.fabricmc:fabric-loader-junit:${property("deps.loader")}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    val properties = mapOf(
        "version" to project.version
    )
    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.release = 25
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

    withSourcesJar()
}

//jar {
//    from("LICENSE") {
//        rename { "${it}_${project.archivesBaseName}"}
//    }
//}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {

    }
}
