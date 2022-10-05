package io.github.a26197993b77e31a4

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject


abstract class ObfustringExtension @Inject constructor(project: Project) {
    val packageKey: Property<String> = project.objects.property(String::class.java)

    companion object {


        const val obfustringConfName = "obfustring"
        const val obfustringTaskName = "obfustringRelease"

        const val assembleTaskName = "assembleRelease"
        const val bundleTaskName = "bundleRelease"
        const val preReleaseBuildName = "preReleaseBuild"


        val getPluginExtension: (Project) -> ObfustringExtension = { project ->
            project
                .extensions
                .findByName(
                    obfustringConfName
                ) as ObfustringExtension?
                ?: project
                    .extensions
                    .create(
                        obfustringConfName,
                        ObfustringExtension::class.java,
                        project
                    )
        }

        fun createObfustringTask(project: Project, callback: (ObfustringTask) -> Unit) {
            val task = project.tasks.create(
                obfustringTaskName,
                ObfustringTask::class.java
            )
            callback(task)
        }
    }
}