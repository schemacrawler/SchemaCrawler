/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.property;

/** JVM CPU architecture information. */
public final class JvmArchitectureInfo extends BaseProductVersion {

  private static final long serialVersionUID = 4051323422934251828L;

  private static final JvmArchitectureInfo JVM_ARCH_INFO = new JvmArchitectureInfo();

  public static JvmArchitectureInfo jvmArchitectureInfo() {
    return JVM_ARCH_INFO;
  }

  private static String jvmArch() {
    final String arch = System.getProperty("os.arch");
    final String archDescription;
    switch (arch) {
      case "x86":
        archDescription = "32-bit x86";
        break;
      case "amd64":
      case "x86_64":
        archDescription = "64-bit x86-64";
        break;
      case "arm":
        archDescription = "32-bit ARM";
        break;
      case "aarch64":
        archDescription = "64-bit ARM";
        break;
      case "risc":
        archDescription = "RISC (Reduced Instruction Set Computer)";
        break;
      case "sparc":
        archDescription = "SPARC (Scalable Processor Architecture)";
        break;
      case "ppc":
      case "powerpc":
        archDescription = "PowerPC";
        break;
      case "mips":
        archDescription = "MIPS (Microprocessor without Interlocked Pipeline Stages)";
        break;
      default:
        archDescription = "Unknown";
        break;
    }
    return String.format("%s (%s)", archDescription, arch);
  }

  private JvmArchitectureInfo() {
    super("JVM Architecture", jvmArch());
  }
}
