/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package io.github.c0nnor263

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * A simple unit test for the 'io.github.c0nnor263.greeting' plugin.
 */
class TestPluginPluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.github.c0nnor263.greeting")

        // Verify the result
        assertNotNull(project.tasks.findByName("greeting"))
    }
}
