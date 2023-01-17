package io.github.l238e4e6fe89ed7f56e89fd28b0d75ac2ba9d72577d98a753a1c457

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject


abstract class ObfustringExtension @Inject constructor(project: Project) {
    val packageKey: Property<String> = project.objects.property(String::class.java)

    companion object {
        const val PLUGIN_CONFIG_BLOCK_NAME = "obfustring"
        const val TASK_NAME = "obfustringRelease"

        const val ASSEMBLE_TASK_NAME = "assembleRelease"
        const val BUNDLE_TASK_NAME = "bundleRelease"


        val get: (Project) -> ObfustringExtension = { project ->
            project.extensions.findByName(PLUGIN_CONFIG_BLOCK_NAME) as ObfustringExtension?
                ?: project.extensions.create(
                    PLUGIN_CONFIG_BLOCK_NAME,
                    ObfustringExtension::class.java,
                    project
                )
        }

        fun createTask(project: Project, callback: (ObfustringTask) -> Unit) {
            val task = project.tasks.create(
                TASK_NAME, ObfustringTask::class.java
            )
            callback(task)
        }
    }
}