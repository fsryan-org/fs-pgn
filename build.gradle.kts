import fsryan.fsryanMavenRepoPassword
import fsryan.fsryanMavenUser
import fsryan.fsryanNPMRegistryUrl
import fsryan.fsryanNPMRegistryToken
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI
import com.android.build.gradle.LibraryExtension as AndroidLibraryExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kover) apply false
}

group = "com.fsryan.chess"
version = "0.0.6"

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

    group = rootProject.group
    version = rootProject.version

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

    tasks.whenTaskAdded {
        if (name == "jsNodeProductionLibraryDistribution") {
            val npmBuild = this
            println("Adding NPM publishing tasks")
            tasks {
                val npmBuildDir = layout.buildDirectory.dir("npm").get().asFile

                val prepNpm = register<Copy>("fsPrepNPM") {
                    dependsOn(npmBuild)
                    mustRunAfter(npmBuild)
                    from(npmBuild.outputs.files)
                    into(npmBuildDir)
                    description = "Move all files into a directory for the purpose of NPM publishing"
                    group = "FS NPM"
                }

                val createNpmrc = create("fsCreateNPMRC") {
                    dependsOn(prepNpm)
                    description = "Writes the .npmrc file necessary"
                    group = "FS NPM"
                    doLast {
                        npmBuildDir.mkdirs()
                        val npmrc = File(npmBuildDir, ".npmrc")
                        val npmRepoWithoutProtocol = fsryanNPMRegistryUrl(includeProtocol = false)
                        npmrc.writeText(
                            """
                            registry=${fsryanNPMRegistryUrl()}
                            ${npmRepoWithoutProtocol}:always-auth=true
                            ${npmRepoWithoutProtocol}:email=not.valid@email.com
                            ${npmRepoWithoutProtocol}:_authToken=${fsryanNPMRegistryToken()}
                            """.trimIndent()
                        )
                    }
                }

                register("fsPublishNPM") {
                    dependsOn(createNpmrc)
                    description = "Publish the NPM package to the FS Ryan NPM registry"
                    group = "FS NPM"
                    doLast {
                        exec {
                            workingDir = npmBuildDir
                            commandLine("npm", "publish")
                        }
                    }
                }
            }
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