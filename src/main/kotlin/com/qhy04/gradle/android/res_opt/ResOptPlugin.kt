package com.qhy04.gradle.android.res_opt

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance
import java.nio.file.Paths

class ResOptPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AndroidBasePlugin::class.java) {
            project.extensions.configure(AndroidComponentsExtension::class.java) {
                onVariants { variant ->
                    if (variant.buildType != "release") return@onVariants
                    val name = variant.name
                    val ext = project.extensions.getByType(CommonExtension::class.java)
                    val aapt2 = Paths.get(
                        sdkComponents.sdkDirectory.get().toString(), "build-tools", ext.buildToolsVersion, "aapt2"
                    )
                    val workdir = Paths.get(
                        project.layout.buildDirectory.get().toString(), "intermediates", "optimized_processed_res", name
                    ).toFile()
                    val zip =
                        if (variant.flavorName.isNullOrEmpty()) "resources-${variant.buildType}-optimize.ap_" else "resources-${variant.flavorName}-${variant.buildType}-optimize.ap_"
                    val optimized = "$zip.opt"
                    project.afterEvaluate {
                        val injected = objects.newInstance<Injected>()
                        tasks.getByPath("optimize${name.replaceFirstChar { c -> c.uppercase() }}Resources").doLast {
                            val cmd = injected.exec.exec {
                                commandLine(
                                    aapt2,
                                    "optimize",
                                    "--collapse-resource-names",
                                    "-o",
                                    optimized,
                                    zip
                                )
                                workingDir = workdir
                                isIgnoreExitValue = true
                            }
                            if (cmd.exitValue == 0) {
                                injected.filesystem.copy {
                                    from(workdir.resolve(optimized))
                                    rename { zip }
                                    into(workdir)
                                }
                                injected.filesystem.delete {
                                    delete(workdir.resolve(optimized))
                                }
                            } else {
                                println("Failed to optimize $name resources")
                            }
                        }
                    }
                }
            }
        }
    }
}
