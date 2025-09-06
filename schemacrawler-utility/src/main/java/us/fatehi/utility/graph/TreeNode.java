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
import java.util.Collections;
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
  private final T value;

  private final List<TreeNode<?>> children = new ArrayList<>();

  public TreeNode(final String name, final T value) {
    this.name = name;
    this.value = value;
  }

  public TreeNode<?> addChild(final TreeNode<?> child) {
    if (child != null) {
      children.add(child);
    }
    return child;
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

      final String indent = String.join("", Collections.nCopies(indentLevel, "  "));

      if (isClosing) {
        // Close children block
        continue;
      }

      sb.append(indent)
          .append("- ")
          .append(node.name)
          .append(": ")
          .append(node.value == null ? "" : node.value)
          .append("\n");

      if (!node.children.isEmpty()) {
        sb.append(indent).append("  _:\n");
        // Push children in reverse to preserve order
        for (int i = node.children.size() - 1; i >= 0; i--) {
          stack.push(new NodeLevel(node.children.get(i), indentLevel + 2, false));
        }
      }
    }

    return sb.toString();
  }
}
