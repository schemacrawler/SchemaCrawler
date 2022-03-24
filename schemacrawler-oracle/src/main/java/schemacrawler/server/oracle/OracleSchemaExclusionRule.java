/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.server.oracle;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import schemacrawler.inclusionrule.InclusionRule;

public final class OracleSchemaExclusionRule implements InclusionRule {

  private static final long serialVersionUID = 4955209955094408513L;

  private static final List<String> schemaExclusions =
      Arrays.asList(
          "ANONYMOUS",
          "APEX_PUBLIC_USER",
          "APPQOSSYS",
          "AUDSYS",
          "BI",
          "CTXSYS",
          "DBSNMP",
          "DIP",
          "DVF",
          "DVSYS",
          "EXFSYS",
          "FLOWS_FILES",
          "GSMADMIN_INTERNAL",
          "HR",
          "IX",
          "LBACSYS",
          "MDDATA",
          "MDSYS",
          "MGMT_VIEW",
          "OE",
          "OLAPSYS",
          "ORACLE_OCM",
          "ORDPLUGINS",
          "ORDSYS",
          "OUTLN",
          "OWBSYS",
          "PM",
          "RDSADMIN",
          "SCOTT",
          "SH",
          "SI_INFORMTN_SCHEMA",
          "SPATIAL_CSW_ADMIN_USR",
          "SPATIAL_WFS_ADMIN_USR",
          "SYS",
          "SYSMAN",
          "TSMSYS",
          "WKPROXY",
          "WKSYS",
          "WK_TEST",
          "WMSYS",
          "XDB",
          "XS$NULL",
          "\"SYSTEM\"");
  private static final Pattern[] schemaExclusionPatterns =
      new Pattern[] {
        Pattern.compile("APEX_[0-9]{6}"), Pattern.compile("FLOWS_[0-9]{5,6}"),
      };

  /** {@inheritDoc} */
  @Override
  public boolean test(final String text) {

    if (schemaExclusions.contains(text)) {
      return true;
    }

    for (final Pattern schemaExclusionPattern : schemaExclusionPatterns) {
      if (schemaExclusionPattern.matcher(text).matches()) {
        return true;
      }
    }

    return false;
  }
}
