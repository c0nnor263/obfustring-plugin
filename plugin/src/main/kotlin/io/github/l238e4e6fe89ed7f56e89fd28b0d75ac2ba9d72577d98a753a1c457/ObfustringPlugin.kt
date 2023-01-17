package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ObfustringPlugin : Plugin<Project> {
    override fun apply(pluginProject: Project) {
        val extension = ObfustringExtension.get(pluginProject)

        if (pluginProject.tasks.findByName(ObfustringExtension.TASK_NAME) == null) {
            ObfustringExtension.createTask(pluginProject) { task ->

                pluginProject.tasks.whenTaskAdded { releaseTask ->
                    if (
                        releaseTask.name == ObfustringExtension.ASSEMBLE_TASK_NAME ||
                        releaseTask.name == ObfustringExtension.BUNDLE_TASK_NAME
                    ) {
                        releaseTask.dependsOn(task)
                    }
                }
                task.apply {
                    key.set(extension.packageKey)
                }
            }
        }
    }
}
