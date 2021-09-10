package com.timecat.plugin.report.analysis.task

import com.timecat.plugin.report.analysis.convert.NodeConvert
import com.timecat.plugin.report.analysis.ext.LibraryAnalysisExtension
import com.timecat.plugin.report.analysis.model.FileDictionary
import com.timecat.plugin.report.analysis.model.Library
import com.timecat.plugin.report.analysis.render.HtmlRenderer
import com.timecat.plugin.report.analysis.render.OutputModuleList
import com.timecat.plugin.report.analysis.render.TextRenderer
import com.timecat.plugin.report.analysis.util.Logger
import com.timecat.plugin.report.analysis.util.PackageChecker
import com.timecat.plugin.report.analysis.util.ResourceUtils
import com.timecat.plugin.report.analysis.util.Timer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.diagnostics.DependencyReportTask
import org.gradle.api.tasks.diagnostics.internal.ReportRenderer
import org.gradle.api.tasks.diagnostics.internal.dependencies.AsciiDependencyReportRenderer
import org.gradle.api.tasks.diagnostics.internal.graph.nodes.RenderableModuleResult

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class DependencyTreeReportTask extends DependencyReportTask {
    def renderer = new AsciiDependencyReportRenderer()

    @Internal
    Configuration dependencyConfiguration
    @Internal
    LibraryAnalysisExtension dependencyExtension

    @Override
    public ReportRenderer getRenderer() {
        return renderer
    }

    Configuration getDependencyConfiguration() {
        return dependencyConfiguration
    }

    void setDependencyConfiguration(Configuration dependencyConfiguration) {
        this.dependencyConfiguration = dependencyConfiguration
    }

    LibraryAnalysisExtension getDependencyExtension() {
        return dependencyExtension
    }

    void setDependencyExtension(LibraryAnalysisExtension dependencyExtension) {
        this.dependencyExtension = dependencyExtension
    }

    @Override
    public void generate(Project project) throws IOException {
        def timer = new Timer()

        try {
            outputHtml()

            if (dependencyExtension.showTree) {
                renderer.startConfiguration(dependencyConfiguration)
                renderer.render(dependencyConfiguration)
                renderer.completeConfiguration(dependencyConfiguration)
            }
        } catch (Exception e) {
            Logger.W.log("generate report file failed!!! ERROR: " + e.message)
            if (dependencyExtension.log) {
                e.printStackTrace()
            }
        }

        timer.mark(Logger.W, "${getName()} total")
    }

    private void outputHtml() {
        def timer = new Timer()

        def output = prepareOutputPath()
        ResourceUtils.copyResources(output)

        timer.mark(Logger.W, "copy resources")

        def resolutionResult = dependencyConfiguration.getIncoming().getResolutionResult()
        def dep = new RenderableModuleResult(resolutionResult.getRoot())

        timer.mark(Logger.W, "get dependencies")

//        def root = Node.create(dep)

//        timer.mark(Logger.W, "create nodes")

        // 通过依赖文件创建依赖字典
        def packageChecker = new PackageChecker()

//        def fs = configuration.getIncoming().getFiles()
        def fs = dependencyConfiguration.fileCollection {
            !(it instanceof DefaultProjectDependency)
        }
        def dictionary = new FileDictionary(fs)

//        root.supplyInfo(extension, dictionary, packageChecker)
        def rootLib = Library.create(dep, dictionary)

        timer.mark(Logger.W, "create root library")

        dependencyExtension.ignore?.each {
            rootLib.applyIgnoreLibrary(it)
        }

        def root = NodeConvert.convert(
                rootLib,
                NodeConvert.Args.get(dictionary)
                        .extension(dependencyExtension)
                        .checker(packageChecker)
                        .brief(!dependencyExtension.fullTree)
        )

        timer.mark(Logger.W, "create root node")

        def msg = packageChecker.outputPackageRepeatList()
        def list = outputModuleList(rootLib, packageChecker)
        list.modules.each {
            Logger.D?.log("module: ${it.name}")
        }

        timer.mark(Logger.W, "output module list")

        if (dependencyExtension.output.contains("html")) {
            def result = new HtmlRenderer(output).render(root, list, msg, dependencyExtension)
            if (msg && !msg.isEmpty()) {
                println msg
            }
            Logger.W?.log("Html output: ${result}")

            timer.mark(Logger.W, "output html file")
        }

        if (dependencyExtension.output.contains("txt")) {
            def result = new TextRenderer(output).render(root, list, msg)
            Logger.W?.log("Txt output: ${result}")

            timer.mark(Logger.W, "output txt file")
        }
    }

    static OutputModuleList outputModuleList(Library root, PackageChecker checker) {
        OutputModuleList list = new OutputModuleList()
        root.contains?.each {
            if (!it.file) {
                list.addModule(new OutputModuleList.DependencyOutput(it.id, 0, "",
                        "pom", "",
                        it.contains.size(), it.useCount, it.useCountImmediate, ""))
                return
            }
            def pkgName = checker.parseModuleName(it.id, it.file.file)
            def isRepeat = checker.isRepeatPackage(pkgName)
            list.addModule(new OutputModuleList.DependencyOutput(it.id, it.file.size, pkgName,
                    it.file.type, isRepeat ? "package name repeat" : "",
                    it.contains.size(), it.useCount, it.useCountImmediate, isRepeat ? "danger" : ""))
        }
        list.sortModules()
        list
    }

    @Deprecated
    static OutputModuleList outputModuleList(FileDictionary dictionary, PackageChecker checker) {
        OutputModuleList list = new OutputModuleList()
        dictionary.cacheInfoMap.each {
            key, value ->
                def pkgName = checker.parseModuleName(key, value.file)
                def isRepeat = checker.isRepeatPackage(pkgName)
                list.addModule(new OutputModuleList.DependencyOutput(key, value.size, pkgName,
                        value.type, isRepeat ? "package name repeat" : "", 0, 0, 0, isRepeat ? "danger" : ""))
        }
        list.sortModules()
        list
    }

    private String prepareOutputPath() {
        def path = "${project.buildDir}/${dependencyExtension.outputPath}/${dependencyConfiguration.name}"
        def file = new File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        path
    }

}