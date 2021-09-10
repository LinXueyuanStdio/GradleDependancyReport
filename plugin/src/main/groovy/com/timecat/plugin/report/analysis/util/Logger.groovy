package com.timecat.plugin.report.analysis.util;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class Logger {

    public static Logger D = new Logger()
    public static Logger W = new Logger()

    private Logger() {
    }

    void log(def message) {
        print "LibReport "
        println message
    }
}
