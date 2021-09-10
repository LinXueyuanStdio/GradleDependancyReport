package com.timecat.plugin.report.analysis.render

import com.google.gson.Gson
import com.timecat.plugin.report.analysis.ext.LibraryAnalysisExtension
import com.timecat.plugin.report.analysis.model.Node
import com.timecat.plugin.report.analysis.util.ResourceUtils
import groovy.json.JsonOutput

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class HtmlRenderer {

    private static final GSON = new Gson()

    private String targetDir

    HtmlRenderer(target) {
        this.targetDir = target
    }

    public String render(Node root, OutputModuleList list, String msg, LibraryAnalysisExtension ext) {
        String json = root ? "[${GSON.toJson(root)}]" : '[]'
        if (msg && msg.length() > 0) {
            msg = msg.replace("\r\n", "<br>")
        } else {
            msg = ""
        }

        def modules = JsonOutput.toJson(list)
        def target = new File(targetDir, "Tree.html")
        def support = ext.showSupport ? ResourceUtils.getTemplateFileContent("support.html") : ""

        def html = ResourceUtils.getTemplateFileContent("Tree.html")
                .replace("%output_support%", support)
                .replace("%output_msg%", msg)
                .replace("%data%", modules)
                .replace("%title%", root.id)
                .replace("%nodes%", json)
        target.setText(html, "UTF-8")

        target.path
    }

}