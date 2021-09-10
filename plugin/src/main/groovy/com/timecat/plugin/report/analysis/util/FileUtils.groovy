package com.timecat.plugin.report.analysis.util;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class FileUtils {

    static String convertFileSize(long size) {
        long kb = 1024
        long mb = kb * 1024
        long gb = mb * 1024

        if (size >= gb) {
            String.format("%.3f GB", (float) size / gb)
        } else if (size >= mb) {
            float f = (float) size / mb
            String.format(f > 100 ? "%.0f MB" : "%.3f MB", f)
        } else if (size >= kb) {
            float f = (float) size / kb;
            String.format(f > 100 ? "%.0f KB" : "%.3f KB", f)
        } else {
            String.format("%d B", size)
        }
    }

    static String getFileType(String fileName) {
        int index = fileName.lastIndexOf(".")
        if (index >= 0) {
            return fileName.substring(index + 1)
        }
        fileName
    }

}
