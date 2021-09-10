package com.timecat.plugin.report.analysis.render

import com.timecat.plugin.report.analysis.util.FileUtils

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class OutputModuleList {

    List<DependencyOutput> modules = new ArrayList<>()

    void sortModules(Closure closure = {
        first, two ->
            two.sizeValue - first.sizeValue
    }) {
        modules.sort(closure)

    }

    void addModule(DependencyOutput output) {
        if (!modules.contains(output)) {
            modules.add(output)
        }
    }

    static class DependencyOutput {

        String name
        String size
        String type
        String pkgName
        String extInfo
        String level
        // 被使用次数
        int useCount
        // 直接依赖次数
        int useCountImmediate
        // 依赖库个数
        int libCount
        long sizeValue

        DependencyOutput(String name, long size, String pkgName, String type, String extInfo,
                         int libCount = 0, int useCount = 0, int useCountImmediate = 0, String level = "") {
            this.name = name
            this.sizeValue = size
            this.size = FileUtils.convertFileSize(size)
            this.pkgName = pkgName
            this.type = type
            this.extInfo = extInfo
            this.level = level
            this.libCount = libCount
            this.useCount = useCount
            this.useCountImmediate = useCountImmediate
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            DependencyOutput that = (DependencyOutput) o

            if (name != that.name) return false

            return true
        }

        int hashCode() {
            return (name != null ? name.hashCode() : 0)
        }
    }

}
