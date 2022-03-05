package com.ice.good.lib.plugin


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

//使用buildSrc 只需要在使用插件的工程里添加apply
//plugin_hello需要在项目的build.gradle里配置
class TinyPngPlugin implements Plugin<Project> {

    //Gradle（初始化、配置、执行三个阶段）配置阶段就会触发，只有build.gradle里的action代码是在执行阶段运行
    @Override
    void apply(Project project) {
        //因为TinyPngPTransform getScopes包含了 Scope.SUB_PROJECTS 所以这里需要判断是不是application
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw new ProjectConfigurationException("plugin:com.android.application must be apply", null)

        }
        //这里使用了project.android（就是app build.gradle里的android{}）,所以在apply plugin时要写在build.gradle的后面
        project.android.registerTransform(new TinyPngPTransform(project))

        //def android = project.extensions.findByType(AppExtension.class)
        //android.registerTransform(new TinyPngPTransform(project))

    }
}