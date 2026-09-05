plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.0.0-beta.7.3"

// https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val project = project(":${node.metadata.project}")
    swaps["minecraft"] = node.metadata.project
    swaps["minecraft_version"] = "\"${project.property("deps.minecraft")}\";"
    swaps["version_string"] = "\"${project.property("meta.mc_version")}\";"
}