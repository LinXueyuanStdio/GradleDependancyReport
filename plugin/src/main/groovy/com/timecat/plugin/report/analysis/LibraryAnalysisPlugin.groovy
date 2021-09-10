package com.timecat.plugin.report.analysis

import com.timecat.plugin.report.analysis.ext.LibraryAnalysisExtension
import com.timecat.plugin.report.analysis.util.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 * 1. 白名单
 * 2. 数据统计
 * 3. 共用标识
 */
class LibraryAnalysisPlugin implements Plugin<Project> {
    private static final EXTENSION_NAME = 'libReport'
    private static final BASE_GROUP = 'reporting'
    private static final TASK_PREFIX = 'libReport'

    private LibraryAnalysisExtension extension

    @Override
    void apply(Project project) {
        extension = project.extensions.create(EXTENSION_NAME, LibraryAnalysisExtension)

        project.afterEvaluate {
            createTask(project)
        }
    }

    void createTask(Project project) {
        def configurations = project.configurations

        configurations.findAll {
            return !it.allDependencies.isEmpty() && getConfigurationSize(it) > 0
        }.each {
            def conf = it.getName()
            def task = project.tasks.create(genTaskName(conf), com.timecat.plugin.report.analysis.task.DependencyTreeReportTask)
            task.dependencyConfiguration = it
            task.group = BASE_GROUP
            task.dependencyExtension = extension
            if (!extension.log) {
                Logger.D = null
            }
        }
    }

    static String genTaskName(String name) {
        char[] arr = name.toCharArray()
        if (arr[0].lowerCase) {
            arr[0] = Character.toUpperCase(arr[0])
            return "${TASK_PREFIX}${String.valueOf(arr)}"
        } else {
            return "${TASK_PREFIX}${name}"
        }
    }

    static int getConfigurationSize(Configuration conf) {
        try {
            return conf.incoming.resolutionResult.allDependencies.size()
        } catch (Exception e) {
            // ignore
        }
        return 0
    }
}
