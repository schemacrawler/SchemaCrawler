/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TreeNode<T> {

  private static class NodeLevel {
    private final TreeNode<?> node;
    private final int level;
    private final boolean isClosing;

    NodeLevel(final TreeNode<?> node, final int level, final boolean isClosing) {
      this.node = node;
      this.level = level;
      this.isClosing = isClosing;
    }
  }

  private final String name;
  private final String value;

  private final List<TreeNode<?>> children = new ArrayList<>();

  public TreeNode(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  public void addChild(final TreeNode<?> child) {
    children.add(child);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    final Deque<NodeLevel> stack = new ArrayDeque<>();
    stack.push(new NodeLevel(this, 0, false)); // false = not yet processed

    while (!stack.isEmpty()) {
      final NodeLevel current = stack.pop();
      final TreeNode<?> node = current.node;
      final int indentLevel = current.level;
      final boolean isClosing = current.isClosing;

      final String indent = "  ".repeat(indentLevel);

      if (isClosing) {
        // Close children block
        continue;
      }

      sb.append(indent).append("- name: ").append(node.name).append("\n");
      sb.append(indent)
          .append("  value: ")
          .append(node.value == null ? "" : node.value)
          .append("\n");

      if (!node.children.isEmpty()) {
        sb.append(indent).append("  children:\n");
        // Push children in reverse to preserve order
        for (int i = node.children.size() - 1; i >= 0; i--) {
          stack.push(new NodeLevel(node.children.get(i), indentLevel + 2, false));
        }
      }
    }

    return sb.toString();
  }
}
