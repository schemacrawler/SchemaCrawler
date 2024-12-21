/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
