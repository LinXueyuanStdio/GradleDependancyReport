package com.timecat.plugin.report.analysis.model

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class FileInfo {
    String id
    long size
    String type
    File file

    FileInfo(String id, long size, String type, File file) {
        this.id = id
        this.size = size
        this.type = type
        this.file = file
    }

}
