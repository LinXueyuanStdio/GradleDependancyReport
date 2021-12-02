package com.timecat.plugin.report.analysis.util
/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class ResourceUtils {

    static final RESOURCE_PATH = "/com/timecat/plugin/report/analysis/"
    static final RESOURCE_FILES = [
            "css/",
            "css/about.css",
            "css/app.css",
            "css/chunk-vendors.css",

            "data/",
            "data/list.json",
            "data/tree.json",
            "data/records.json",

            "fonts/",
            "fonts/element-icons.woff",
            "fonts/element-icons.ttf",

            "js/",
            "js/about.js",
            "js/about.js.map",
            "js/app.js",
            "js/app.js.map",
            "js/chunk-vendors.js",
            "js/chunk-vendors.js.map",

            "index.html",
            "favicon.ico",

            //before
            "css/demo.css",
            "css/z/ztree.css",
            "css/z/img/line_conn.gif",
            "css/z/img/loading.gif",
            "css/z/img/zTreeStandard.gif",
            "css/z/img/zTreeStandard.png",
            "css/z/img/jar.png",
            "css/z/img/aar.png",

            "js/jquery.ztree.core.min.js",
            "js/jquery.min.js",
            "js/cytoscape-dagre.js",
            "js/dagre.min.js"
    ]

    private static obj = new ResourceUtils()

    private ResourceUtils() {
    }

    static void copyResourcesTo(String targetPath) {
        RESOURCE_FILES.each {
            if (it.endsWith('/')) {
                new File(targetPath, it).mkdirs()
                return
            }

            def target = new File(targetPath, it)
            if (!target.exists()) {
                def source = obj.getClass().getResourceAsStream("${RESOURCE_PATH}${it}")
                target.withDataOutputStream {
                    os -> os << source
                }
            }
        }
    }

    static String getTemplateFileContent(String fileName) {
        return obj.getClass().getResourceAsStream("${RESOURCE_PATH}${fileName}").getText("UTF-8")
    }

}
