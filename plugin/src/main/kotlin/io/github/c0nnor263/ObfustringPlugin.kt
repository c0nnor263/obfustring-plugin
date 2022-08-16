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
                        "obfustringRelease",
                        ObfustringTask::class.java
                    ) { task ->
                        project.tasks.whenTaskAdded { releaseTask ->
                            if (
                                releaseTask.name == "assembleRelease" ||
                                releaseTask.name == "bundleRelease"
                            ) {
                                releaseTask.dependsOn(task)
                            }
                        }

                        val packageName = variant.applicationId.get()
                        task.packageKey = packageName.filter { it != '.' }
                    }
                }

            }
    }
}