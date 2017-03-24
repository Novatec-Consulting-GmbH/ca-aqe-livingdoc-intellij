package org.livingdoc.intellij.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.livingdoc.intellij.common.NodeType;

import javax.swing.*;
import java.io.Serializable;

/**
 * Parent class for the nodes of the repository view tree
 *
 * @see NodeType
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 4522875652776261867L;
    private NodeType type;
    private Node parent;
    private String name;
    private Icon icon;

    public Node() {
        parent = null;
        type = null;
    }

    public Node(final String nodeName, final Icon nodeIcon, final NodeType nodeType, final Node parent) {
        this.name = nodeName;
        this.icon = nodeIcon;
        this.type = nodeType;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("icon", icon)
                .append("type", type)
                .append("parent", parent)
                .toString();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(final Icon icon) {
        this.icon = icon;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(final NodeType type) {
        this.type = type;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(final Node parent) {
        this.parent = parent;
    }
}
