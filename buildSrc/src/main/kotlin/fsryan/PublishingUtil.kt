package fsryan

import fsryan.BuildProperties.evaluateProperty
import org.gradle.api.Project

fun Project.fsryanMavenUser(): String {
    return project.evaluateProperty(
        propName = "com.fsryan.fsryan_maven_repo_user",
        envVarName = "FSRYAN_MAVEN_USER"
    )
}

fun Project.fsryanMavenRepoPassword(release: Boolean = true): String {
    if (release) {
        return project.evaluateProperty(
            propName = "com.fsryan.fsryan_release_password",
            envVarName = "FSRYAN_MAVEN_RELEASE_PASSWORD"
        )
    }
    return project.evaluateProperty(propName = "com.fsryan.fsryan_snapshot_password", envVarName = "FSRYAN_MAVEN_SNAPSHOT_PASSWORD")
}

fun Project.fsryanNPMRepo(includeProtocol: Boolean = true): String {
    var npmUrl = project.evaluateProperty(propName = "com.fsryan.npm_registry_url", envVarName = "FSRYAN_NPM_URL")
    npmUrl = if (includeProtocol) npmUrl else npmUrl.removePrefix("https:")
    val registryName = project.evaluateProperty(propName = "com.fsryan.npm_registry_name", envVarName = "FSRYAN_NPM_REGISTRY_NAME")
    return "${npmUrl}/$registryName/"
}

fun Project.fsryanNPMRepoToken(): String {
    return project.evaluateProperty(propName = "com.fsryan.npm_registry_token", envVarName = "FSRYAN_NPM_REPO_PASSWORD")
}