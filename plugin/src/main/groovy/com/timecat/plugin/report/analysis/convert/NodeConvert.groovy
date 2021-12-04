package com.timecat.plugin.report.analysis.convert

import com.timecat.plugin.report.analysis.ext.LibraryAnalysisExtension
import com.timecat.plugin.report.analysis.model.FileDictionary
import com.timecat.plugin.report.analysis.model.FileInfo
import com.timecat.plugin.report.analysis.model.Library
import com.timecat.plugin.report.analysis.util.PackageChecker
import com.timecat.plugin.report.analysis.util.Logger
import com.timecat.plugin.report.analysis.util.FileUtils
import com.timecat.plugin.report.analysis.model.Node

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class NodeConvert {

    static class Args {
        FileDictionary fileDictionary
        LibraryAnalysisExtension ext
        PackageChecker checker
        boolean isBrief = true

        Args(FileDictionary dic) {
            this.fileDictionary = dic
        }

        static Args get(FileDictionary dic) {
            return new Args(dic)
        }

        Args fileDictionary(FileDictionary dic) {
            this.fileDictionary = dic
            this
        }

        Args extension(LibraryAnalysisExtension ext) {
            this.ext = ext
            this
        }

        Args checker(PackageChecker checker) {
            this.checker = checker
            this
        }

        Args brief(boolean isBrief) {
            this.isBrief = isBrief
            this
        }
    }

    static setNodeCanRemove(Node node) {
        if (node && node.id == node.detail) {
            node.canRemove = true
        }
    }

    static id = 0

    static Node convert(Library lib, Args args, Set<Object> cacheIds = new HashSet<>()) {
        boolean hasAdded = cacheIds.contains(lib.id)
        boolean hasChildren = !lib.children.isEmpty()

        Node node = new Node()
        node.identity = id++
        node.id = lib.id
        node.detail = lib.name
        if (hasAdded && !lib.children.isEmpty() && args.isBrief) {
            node.iconSkin = "omit" // 表示简略的依赖库集合
        }

        if (!hasAdded) {
            cacheIds?.add(lib.id)
        }
        if (hasChildren && (!args.isBrief || !hasAdded)) {
            if (hasChildren) {
                // 已加入过的兄弟库
                Set<String> ids = new HashSet<>()
                // 已加入的节点
                Map<String, Node> nodes = new HashMap<>()
                // 已添加过的所有依赖
                Set<Library> libs = new HashSet<>()

                lib.children.each {
                    Node child = convert(it, args, cacheIds)
                    node.addNode(child)

                    // 当前依赖库的子库中是否包含兄弟库
                    it.containIds.findAll {
                        key ->
                            // 找到重复的依赖库id集合
                            ids.contains(key)
                    }.each {
                        key ->
                            // 标记重复
                            setNodeCanRemove(nodes.get(key))
//                            nodes.get(key)?.canRemove = true
                    }

                    // 当前依赖库是否已在其他库中加入过
                    if (libs.contains(it)) {
                        setNodeCanRemove(child)
//                        child.canRemove = true
                    }

                    ids.add(it.id)
                    nodes.put(child.id, child)
                    libs.add(it)
                    libs.addAll(it.contains)
                }

                // 对可移除的库进行标记
                node.children?.each {
                    if (it.canRemove) {
                        it.name = "<s>${it.name}</s>"
                        Logger.D?.log("${it.id} is can remove.")
                    }
                }
            }
        }

        node.open = !args.isBrief || !hasAdded
        node.totalSize = lib.getTotalSizeWithoutIgnore()

        // 存在依赖信息
        FileInfo info = lib.file

        def (txtTotalSize, txtSize, txtType) = outputTxt(info, lib, args, node)

        // Android package name
        def packageName = parsePackageName(info, args.checker)

        // 累计依赖库大小 + 当前依赖库大小 + 依赖库名称
        node.name = "${txtTotalSize}${txtSize ?: ''} ${node.detail} ${packageName ?: ''}"

        if (info) {
            node.fileSize = info.size
            def packageName2 = args.checker.parseModuleName(info.id, info.file)
            // 累计依赖库大小 + 当前依赖库大小 + 依赖库名称
            node.short_name = "${packageName2 ?: ''}"
        }

        node
    }

    private static Object parsePackageName(FileInfo info, PackageChecker checker) {
        def packageName = null
        if (info?.type == 'aar' && checker) {
            packageName = "<span class='tag' style='color:#999'>${checker.parseModuleName(info.id, info.file)}</span>"
        }
        packageName
    }

    private static List outputTxt(FileInfo info, Library lib, Args args, Node node) {
        def txtType = null
        def txtSize = null

        if (info) {
            def styleSize = lib.isIgnoreSize ? "tag-ignore" : (args.ext ? args.ext.getSizeTag(info.size) : 'tag-normal')
            def styleType = "jar" == info.type ? 'type_jar' : 'type_aar'

            txtType = "<span class='type ${styleType}'></span>"
            txtSize = "<span class='tag ${styleSize}'>${txtType}${FileUtils.convertFileSize(info.size)}</span>"

            node.fileSize = info.size
        }

        def styleTotalSize = "class='tag tag-ignore'"
        if (!lib.isIgnoreSize) {
            styleTotalSize = "class='tag' style='color:#fff;background-color:${genColorCode(node.totalSize, args.fileDictionary.totalSize)}'"
        }
        def txtTotalSize = "<span ${styleTotalSize}>${FileUtils.convertFileSize(node.totalSize)}</span>"
        [txtTotalSize, txtSize, txtType]
    }

    private static String genColorCode(long size, long max) {
        if (max == 0L) {
            return "#ffdddd"
        }
        final int color = 0xdd - 0x22
        def c = max - size * 4
        if (c < 0) {
            c = 0
        }

        int result = color * c / max
        result += (result << 8) + 0xff0000
        "#${Integer.toHexString(result)}"
    }

}
