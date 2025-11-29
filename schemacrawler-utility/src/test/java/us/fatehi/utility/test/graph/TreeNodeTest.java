/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test.graph;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;
import us.fatehi.utility.graph.TreeNode;

public class TreeNodeTest {

  @Test
  void deeplyNestedTreeShouldRenderAllLevels() {
    final TreeNode<String> root = new TreeNode<>("root", "system");
    final TreeNode<String> level1 = new TreeNode<>("level1", "A");
    final TreeNode<String> level2 = new TreeNode<>("level2", "B");
    final TreeNode<String> level3 = new TreeNode<>("level3", "C");

    level2.addChild(level3);
    level1.addChild(level2);
    root.addChild(level1);

    final String yaml = root.toString();

    assertThat(yaml, containsString("level3"));
    assertThat(yaml, containsString("C"));
  }

  @Test
  void emptyValueShouldStillRenderKey() {
    final TreeNode<String> node = new TreeNode<>("empty", (String) null);
    final String yaml = node.toString();

    assertThat(yaml, containsString("empty"));
  }

  @Test
  void multiple_ShouldPreserveOrder() {
    final TreeNode<String> root = new TreeNode<>("root", "system");
    root.addChild(new TreeNode<>("first", "1"));
    root.addChild(new TreeNode<>("second", "2"));

    final String yaml = root.toString();

    final int firstIndex = yaml.indexOf("first");
    final int secondIndex = yaml.indexOf("second");

    assertThat(firstIndex, lessThan(secondIndex));
  }

  @Test
  void nestedTreeShouldInclude_Block() {
    final TreeNode<String> root = new TreeNode<>("root", "system");
    final TreeNode<String> child = new TreeNode<>("dev", "sandbox");
    root.addChild(child);

    final String yaml = root.toString();

    assertThat(yaml, containsString("_:"));
    assertThat(yaml, containsString("- dev"));
    assertThat(yaml, containsString("sandbox"));
  }

  @Test
  void singleNodeYAMLShouldContainNameAndValue() {
    final TreeNode<String> node = new TreeNode<>("root", "system");
    final String yaml = node.toString();

    assertThat(yaml, containsString("- root"));
    assertThat(yaml, containsString("system"));
    assertThat(yaml, not(containsString("_:")));
  }
}
