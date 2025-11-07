package dev.alejo.nexo.convention

import org.gradle.api.Project
import java.util.Locale

fun Project.pathToPackageName(): String {
    val relativePackageName = path
        .replace(':', '.')
        .lowercase()

    return "dev.alejo$relativePackageName"
}

fun Project.pathToResourcePrefix(): String {
    return path
        .replace(':', '_')
        .lowercase()
        .drop(1) + "_"
}

fun Project.pathToFrameworkName(): String {
    val parts = this.path.split(':', '-', '_', ' ')
    return parts.joinToString("") { part ->
        part.replaceFirstChar { it.titlecase(Locale.ROOT) }
    }
}