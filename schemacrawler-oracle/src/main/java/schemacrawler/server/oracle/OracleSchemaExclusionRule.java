/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.server.oracle;

import java.io.Serial;
import java.util.Arrays;
import java.util.function.Predicate;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.ListExclusionRule;
import schemacrawler.inclusionrule.RegularExpressionExclusionRule;

public final class OracleSchemaExclusionRule implements InclusionRule {

  @Serial private static final long serialVersionUID = 4955209955094408513L;

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
          .and(new RegularExpressionExclusionRule("FLOWS_[0-9]{5,6}"))
          .and(new RegularExpressionExclusionRule("OPS\\$ORACLE"));

  /** {@inheritDoc} */
  @Override
  public boolean test(final String text) {
    return exclusionRule.test(text);
  }
}
