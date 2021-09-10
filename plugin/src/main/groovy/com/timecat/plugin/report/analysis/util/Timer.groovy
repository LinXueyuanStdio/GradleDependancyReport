package com.timecat.plugin.report.analysis.util;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class Timer {

    long start

    Timer() {
        start = System.currentTimeMillis()
    }

    void mark(Logger logger, String message) {
        long now = System.currentTimeMillis()
        logger?.log "${message ?: ""} cost ${now - start}ms"
        start = now
    }

}
