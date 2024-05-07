import fsryan.fsryanMavenRepoPassword
import fsryan.fsryanMavenUser
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI
import com.android.build.gradle.LibraryExtension as AndroidLibraryExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka) apply false
}

group = "com.fsryan.chess"
version = "0.0.1"

buildscript {
    val props = fsryan.BuildProperties
    props.initializeWith(rootProject)
    repositories {
        maven {
            if (hasProperty("fsryan.includeMavenLocal")) {
                mavenLocal()
            }
            name = "fsryan-release"
            url = uri("https://maven.fsryan.com/fsryan-release")
            credentials(PasswordCredentials::class) {
                username = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_maven_repo_user",
                    envVarName = "FSRYAN_MAVEN_REPO_USER"
                )
                password = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_release_password",
                    envVarName = "FSRYAN_MAVEN_RELEASE_REPO_TOKEN"
                )
            }
        }
        maven {
            name = "fsryan-snapshot"
            url = uri("https://maven.fsryan.com/fsryan-snapshot")
            credentials(PasswordCredentials::class) {
                username = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_maven_repo_user",
                    envVarName = "FSRYAN_MAVEN_REPO_USER"
                )
                password = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_snapshot_password",
                    envVarName = "FSRYAN_MAVEN_SNAPSHOT_REPO_TOKEN"
                )
            }
        }
        mavenCentral()
        google()
    }
}

allprojects {
    repositories {
        val props = fsryan.BuildProperties
        if (hasProperty("fsryan.includeMavenLocal")) {
            mavenLocal()
        }
        maven {
            url = uri("https://maven.fsryan.com/fsryan-release")
            credentials(PasswordCredentials::class) {
                username = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_maven_repo_user",
                    envVarName = "FSRYAN_MAVEN_REPO_USER"
                )
                password = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_release_password",
                    envVarName = "FSRYAN_MAVEN_RELEASE_REPO_TOKEN"
                )
            }
        }
        maven {
            url = uri("https://maven.fsryan.com/fsryan-snapshot")
            credentials(PasswordCredentials::class) {
                username = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_maven_repo_user",
                    envVarName = "FSRYAN_MAVEN_REPO_USER"
                )
                password = props.prop(
                    rootProject,
                    propName = "com.fsryan.fsryan_snapshot_password",
                    envVarName = "FSRYAN_MAVEN_SNAPSHOT_REPO_TOKEN"
                )
            }
        }
        mavenCentral()
        google()
    }

    tasks.withType<DokkaTask>().configureEach {
        dokkaSourceSets.configureEach {
            reportUndocumented.set(true)
            documentedVisibilities.set(listOf(Visibility.PUBLIC, Visibility.INTERNAL))
        }
    }

    afterEvaluate {
        extensions.findByType(PublishingExtension::class)?.apply {
            println("Publishing extension found")
            repositories {
                maven {
                    url = URI("https://maven.fsryan.com/fsryan-release")
                    credentials {
                        username = fsryanMavenUser()
                        password = fsryanMavenRepoPassword()
                    }
                }
            }
        }
        extensions.findByType(AndroidLibraryExtension::class)?.apply {
            println("Android library extension found")
            compileSdk = (findProperty("android.compileSdk") as String).toInt()
            namespace = "com.fsryan.chess"

            defaultConfig {
                minSdk = (findProperty("android.minSdk") as String).toInt()
                lint {
                    targetSdk = (findProperty("android.targetSdk") as String).toInt()
                }
            }
        }
    }
}