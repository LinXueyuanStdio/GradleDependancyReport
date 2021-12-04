package com.timecat.plugin.report.analysis.model


/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class Node {
    int identity
    String id
    String name
    String short_name
    boolean open
    List<Node> children
    long fileSize
    long totalSize
    String iconSkin
    String detail
    String type //"jar" or "aar"
    String groupId
    String artifactId
    int versionCode
    String versionName
    // 可移除的依赖库（重复添加）
    boolean canRemove

    void addNode(Node node) {
        if (node == null) {
            return
        }
        List<Node> children = getChildren()
        if (children == null) {
            children = new ArrayList<>()
            setChildren(children)
        }
        children.add(node)
    }

    int getChildrenSize() {
        children == null ? 0 : children.size()
    }

    boolean hasChildren() {
        return children != null && !children.isEmpty()
    }

    @Override
    String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", open=" + open +
                ", children=" + children +
                ", fileSize=" + fileSize +
                ", totalSize=" + totalSize +
                ", iconSkin='" + iconSkin + '\'' +
                '}'
    }
}
