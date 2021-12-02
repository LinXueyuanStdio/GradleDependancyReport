package com.timecat.plugin.report.analysis.util
/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class ResourceUtils {

    static final RESOURCE_PATH = "/com/timecat/plugin/report/analysis"

    private static obj = new ResourceUtils()

    private ResourceUtils() {
    }

    static void copyResourcesTo(String targetPath) {
        def dir = new File(obj.getClass().getResource(RESOURCE_PATH).toURI())
        def files = dir.listFiles()
        files.each {
            if (it.isDirectory()) {
                copyResourcesTo(targetPath + "/" + it.getName())
            } else {
                def target = new File(targetPath, it.getName())
                if (!target.exists()) {
                    def source = obj.getClass().getResourceAsStream(it.path)
                    target.withDataOutputStream {
                        os -> os << source
                    }
                }
            }
        }
    }

    static String getTemplateFileContent(String fileName) {
        return obj.getClass().getResourceAsStream("${RESOURCE_PATH}/${fileName}").getText("UTF-8")
    }

}
