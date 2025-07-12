/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.property;

/** JVM system information. */
public final class JvmSystemInfo extends BaseProductVersion {

  private static final long serialVersionUID = 4051323422934251828L;

  private static final JvmSystemInfo JVM_SYSTEM_INFO = new JvmSystemInfo();

  public static JvmSystemInfo jvmSystemInfo() {
    return JVM_SYSTEM_INFO;
  }

  private JvmSystemInfo() {
    super(
        String.format(
            "%s %s",
            System.getProperty("java.vm.vendor", "<unknown>"),
            System.getProperty("java.vm.name", "<unknown>")),
        System.getProperty("java.runtime.version", ""));
  }
}
