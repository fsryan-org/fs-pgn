package fsryan

import fsryan.BuildProperties.evaluateProperty
import org.gradle.api.Project

fun Project.fsryanMavenUser(): String {
    return project.evaluateProperty(
        propName = "com.fsryan.fsryan_maven_repo_user",
        envVarName = "FSRYAN_MAVEN_USER"
    )
}

fun Project.fsryanNPMRepo(includeProtocol: Boolean = true): String {
    val npmUrl = project.evaluateProperty(propName = "com.fsryan.npm_registry_url", envVarName = "FSRYAN_NPM_URL")
    val suffix = "//${npmUrl}/" // <-- trailing slash is important
    val prefix = if (includeProtocol) "https:" else ""
    return "$prefix$suffix"
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

fun Project.fsryanNPMRepoPassword(): String {
    return project.evaluateProperty(propName = "fsryan.npm.repo.password", envVarName = "FSRYAN_NPM_REPO_PASSWORD")
}