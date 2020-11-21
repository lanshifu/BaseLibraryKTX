package com.lanshifu.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class ThreadPlugin implements Plugin<Project> {

    void apply(Project project) {
        System.out.println("### ThreadTransform ###")

        def android = project.extensions.getByType(AppExtension)
        System.out.println '----------- registering AutoTrackTransform  -----------'
        ThreadTransform transform = new ThreadTransform()
        android.registerTransform(transform)
    }
}