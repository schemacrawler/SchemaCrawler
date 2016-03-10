/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.lint.executable;


import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.LinterConfig;

public final class LinterDispatchRule
  implements Serializable, Comparable<LinterDispatchRule>
{

  private static final long serialVersionUID = 2740981623064860755L;

  private final String linterId;
  private final LintDispatch dispatch;
  private final int dispatchThreshold;

  public LinterDispatchRule(final LinterConfig linterConfig)
  {
    requireNonNull(linterConfig, "No linter configuration provided");

    linterId = linterConfig.getLinterId();
    dispatch = linterConfig.getDispatch();
    dispatchThreshold = linterConfig.getDispatchThreshold();
  }

  @Override
  public int compareTo(final LinterDispatchRule linterDispatchRule)
  {
    if (linterDispatchRule == null)
    {
      return 1;
    }

    int comparison = 0;

    if (comparison == 0)
    {
      comparison = dispatch.compareTo(linterDispatchRule.getDispatch());
    }

    if (comparison == 0)
    {
      comparison = dispatchThreshold
                   - linterDispatchRule.getDispatchThreshold();
    }

    if (comparison == 0)
    {
      comparison = linterId.compareTo(linterDispatchRule.getLinterId());
    }

    return comparison;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof LinterDispatchRule))
    {
      return false;
    }
    final LinterDispatchRule other = (LinterDispatchRule) obj;
    if (dispatch != other.dispatch)
    {
      return false;
    }
    if (dispatchThreshold != other.dispatchThreshold)
    {
      return false;
    }
    if (linterId == null)
    {
      if (other.linterId != null)
      {
        return false;
      }
    }
    else if (!linterId.equals(other.linterId))
    {
      return false;
    }
    return true;
  }

  public LintDispatch getDispatch()
  {
    return dispatch;
  }

  public void dispatch()
  {
    if (dispatch != null)
    {
      dispatch.dispatch();
    }
  }

  public int getDispatchThreshold()
  {
    return dispatchThreshold;
  }

  public String getLinterId()
  {
    return linterId;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (dispatch == null? 0: dispatch.hashCode());
    result = prime * result + dispatchThreshold;
    result = prime * result + (linterId == null? 0: linterId.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    return "LinterDispatchRule [linterId=" + linterId + ", dispatch=" + dispatch
           + ", dispatchThreshold=" + dispatchThreshold + "]";
  }

}
