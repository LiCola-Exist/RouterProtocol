package com.licola.route.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class PluginLaunch implements Plugin<Project>{

    static Logger logger

    @Override
    void apply(Project project) {

        project.extensions.create("e1", Extension)

        //创建读取外部设置的 task
        project.task("readExt") << {
            println "e1=${project["e1"].testVar}"
        }

        //监听构建事件
//        project.gradle.addListener(new TaskTimeListener())

        def isApp= project.plugins.hasPlugin(AppPlugin)
        if (isApp){
            logger= project.getLogger()

            logger.info("project start plugin")

            def android= project.extensions.getByType(AppExtension)
            def transform= new RegisterTransform()

            android.registerTransform(transform)
        }


    }
}