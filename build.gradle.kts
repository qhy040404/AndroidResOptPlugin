import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.gradle.plugin-publish") version "1.1.0"
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
    idea
}

repositories {
    mavenCentral()
    google()
}

val pluginId = "com.qhy04.gradle.android.res_opt"
val githubUrl = "https://github.com/qhy040404/AndroidResOptPlugin"
val webUrl = "https://github.com/qhy040404/AndroidResOptPlugin"
val projDesc = "Optimize an Android app's resources."

version = "1.0.1"
group = "com.qhy04.gradle"
description = projDesc

configurations {
    register("testRuntimeDependencies") {
        extendsFrom(compileOnly.get())
        attributes {
            // KGP publishes multiple variants https://kotlinlang.org/docs/whatsnew17.html#support-for-gradle-plugin-variants
            attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
        }
    }
    configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin")) {
                useVersion(getKotlinPluginVersion())
            }
        }
    }
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin")
    compileOnly("com.android.tools.build:gradle:8.1.1")
}

kotlin {
    jvmToolchain(17)
}

tasks {
    val generateVersionProperties = register("generateVersionProperties") {
        val projectVersion = version
        val propertiesFile = File(sourceSets.main.get().output.resourcesDir, "version.properties")
        inputs.property("projectVersion", projectVersion)
        outputs.file(propertiesFile)

        doLast {
            propertiesFile.writeText("version = $projectVersion")
        }
    }

    processResources {
        dependsOn(generateVersionProperties)
    }

    withType<JavaCompile>().configureEach {
        options.release.set(JavaVersion.VERSION_17.majorVersion.toInt())
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            apiVersion = "1.9"
            languageVersion = "1.9"
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    wrapper {
        gradleVersion = "8.3"
    }
}

gradlePlugin {
    website.set(webUrl)
    vcsUrl.set(githubUrl)
    plugins {
        create("androidResOptPlugin") {
            id = pluginId
            displayName = "Android Resources Optimization plugin"
            description = project.description
            tags.addAll(listOf("android","optimize","arsc","aapt2","aapt","resource"))
            implementationClass = "com.qhy04.gradle.android.res_opt.ResOptPlugin"
        }
    }
}

java {
    withSourcesJar()
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set(project.name)
            description.set(project.description)
            url.set(webUrl)

            scm {
                url.set(githubUrl)
            }

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("qhy040404")
                    name.set("Justin Qian")
                }
            }
        }
    }
}
