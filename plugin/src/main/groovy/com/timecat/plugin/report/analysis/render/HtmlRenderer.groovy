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

    class Graph {

    }

    public String render(Node root, OutputModuleList list, String msg, LibraryAnalysisExtension ext) {
        // list view
        def target_list = new File(targetDir, "data/list.json")
        def data_list = JsonOutput.toJson(list)
        target_list.setText(data_list, "UTF-8")

        // tree view
        def target_tree = new File(targetDir, "data/tree.json")
        String data_tree = root ? "{\"data\":[${GSON.toJson(root)}]}" : '{\"data\":[]}'
        target_tree.setText(data_tree, "UTF-8")

        if (msg && msg.length() > 0) {
            msg = msg.replace("\r\n", "<br>")
        } else {
            msg = ""
        }

        def support = ext.showSupport ? ResourceUtils.getTemplateFileContent("support.html") : ""

        String json = root ? "[${GSON.toJson(root)}]" : '[]'
        def target = new File(targetDir, "Tree.html")
        def html = ResourceUtils.getTemplateFileContent("Tree.html")
                .replace("%output_support%", support)
                .replace("%output_msg%", msg)
                .replace("%data%", data_list)
                .replace("%title%", root.id)
                .replace("%nodes%", json)
        target.setText(html, "UTF-8")

        target.path
    }

}