package com.licola.route.register

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginLaunch implements Plugin<Project>{

    @Override
    void apply(Project project) {
        System.out.println("------------------开始----------------------");
        System.out.println("这是我们的自定义插件!");
        System.out.println("------------------结束----------------------->");

        def isApp = project.plugins.hasPlugin(AppPlugin)
        if (isApp){
            def android= project.extensions.getByType(AppExtension)
            /**
             * 注册transform
             */
            android.registerTransform(new TestTransform(project))

        }



    }
}