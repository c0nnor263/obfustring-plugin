package io.github.c0nnor263

import org.gradle.api.Plugin
import org.gradle.api.Project


class ObfustringPlugin : Plugin<Project> {
    override fun apply(pluginProject: Project) {
        pluginProject.rootProject.allprojects.forEach { childProject ->

            val extension = ObfustringExtension.getPluginExtension(childProject)

            if (childProject.tasks.findByName(ObfustringExtension.obfustringTaskName) == null) {
                ObfustringExtension.createObfustringTask(childProject) { task ->
                    if (childProject != pluginProject.rootProject) {

                        pluginProject.tasks.whenTaskAdded { releaseTask ->
                            if (
                                releaseTask.name == ObfustringExtension.assembleTaskName ||
                                releaseTask.name == ObfustringExtension.bundleTaskName
                            ) {
                                releaseTask.dependsOn(task)
                                childProject.tasks.whenTaskAdded { childReleaseTask ->
                                    if (
                                        childReleaseTask.name == ObfustringExtension.preReleaseBuildName
                                    ) {
                                        childReleaseTask.dependsOn(task)
                                    }
                                }
                            }
                        }
                        task.key.set(extension.packageKey)
                    }
                }
            }
        }
    }
}
