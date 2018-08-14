package com.licola.route.register

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginLaunch implements Plugin<Project> {


    @Override
    void apply(Project project) {
        System.out.println("------------------开始----------------------")
        System.out.println("这是我们的自定义插件!")

        def android = project.extensions.getByType(AppExtension)
        /**
         * 注册transform
         */
        android.registerTransform(new TestTransform(project))

        project.extensions.create("routeConfig", MyPluginTestConfig)

        def isApp = project.plugins.hasPlugin(AppPlugin)
        if (isApp) {

            android.applicationVariants.all { variant ->
                def variantData = variant.variantData
                def scope = variantData.scope

                //获取 build.gradle中创建的Ext
                def config = project.extensions.getByName("routeConfig")

                def createTaskName = scope.getTaskName("Test-Plugin", "MyTestPlugin")

                def createTask = project.task(createTaskName)


                createTask.doLast {
                    createJavaTest(variant, config)
                }

                def generateBuildConfigTaskName = variant.getVariantData().getScope().getGenerateBuildConfigTask().name
                def generateBuildConfigTask = project.tasks.getByName(generateBuildConfigTaskName)

                if (generateBuildConfigTask) {
                    createTask.dependsOn generateBuildConfigTask
                    generateBuildConfigTask.finalizedBy createTask
                }
            }
        }
        System.out.println("------------------结束----------------------->");

    }

    static def void createJavaTest(variant, config) {
        def content = """ public class MyPluginCreateTestClass{ public static final String str=  "${
            config.str
        }";}"""

        File outputDir = variant.getVariantData().getScope().getBuildConfigSourceOutputDir()

        def javaFile = new File(outputDir, "MyPluginCreateTestClass.java")

        javaFile.write(content, "UTF-8")

    }
}

