package io.github.c0nnor263

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


class ObfustringPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            .onVariants { variant ->
                if (variant.buildType == "release") {

                    project.tasks.create(
                        "obfustring_${variant.buildType}",
                        ObfustringTask::class.java
                    ) { task ->
                        project.tasks.findByName("assembleRelease")?.dependsOn(task)
                        val packageName = variant.applicationId.get()
                        task.packageKey = packageName.filter { it != '.' }
                    }
                }

            }
    }
}