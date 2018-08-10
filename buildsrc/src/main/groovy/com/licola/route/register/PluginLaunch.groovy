package com.licola.route.register

import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginLaunch implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.task('hello') {
            doLast {
                println "Hello from the GreetingPlugin"
            }
        }
    }
}