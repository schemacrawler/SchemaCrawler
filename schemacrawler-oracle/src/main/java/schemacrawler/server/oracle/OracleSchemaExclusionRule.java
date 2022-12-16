/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.function.Predicate;

import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.ListExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;

public final class OracleSchemaExclusionRule implements InclusionRule {

  private static final long serialVersionUID = 4955209955094408513L;

  private static final Predicate<String> exclusionRule =
      new ListExclusionRule(
              Arrays.asList(
                  "ANONYMOUS",
                  "APEX_050000",
                  "APEX_PUBLIC_USER",
                  "APPQOSSYS",
                  "AUDSYS",
                  "BI",
                  "CTXSYS",
                  "DBSFWUSER",
                  "DBSNMP",
                  "DIP",
                  "DVF",
                  "DVSYS",
                  "EXFSYS",
                  "FLOWS_FILES",
                  "GGSYS",
                  "GSMADMIN_INTERNAL",
                  "GSMCATUSER",
                  "GSMUSER",
                  "HR",
                  "IX",
                  "LBACSYS",
                  "MDDATA",
                  "MDSYS",
                  "MGMT_VIEW",
                  "OE",
                  "OLAPSYS",
                  "OPS$ORACLE",
                  "ORACLE_OCM",
                  "ORDDATA",
                  "ORDPLUGINS",
                  "ORDSYS",
                  "OUTLN",
                  "OWBSYS",
                  "PDBADMIN",
                  "PM",
                  "RDSADMIN",
                  "REMOTE_SCHEDULER_AGENT",
                  "SCOTT",
                  "SH",
                  "SI_INFORMTN_SCHEMA",
                  "SPATIAL_CSW_ADMIN_USR",
                  "SPATIAL_WFS_ADMIN_USR",
                  "SYS",
                  "SYS$UMF",
                  "SYSBACKUP",
                  "SYSDG",
                  "SYSKM",
                  "SYSMAN",
                  "SYSRAC",
                  "\"SYSTEM\"",
                  "TSMSYS",
                  "WKPROXY",
                  "WKSYS",
                  "WK_TEST",
                  "WMSYS",
                  "XDB",
                  "XS$NULL"))
          .and(new RegularExpressionExclusionRule("APEX_[0-9]{6}"))
          .and(new RegularExpressionExclusionRule("FLOWS_[0-9]{5,6}"));

  /** {@inheritDoc} */
  @Override
  public boolean test(final String text) {
    return exclusionRule.test(text);
  }
}
