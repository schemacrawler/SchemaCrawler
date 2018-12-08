/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.utility;


import static java.nio.file.Files.exists;
import static java.nio.file.Files.size;
import static java.util.Objects.requireNonNull;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static sf.util.Utility.isBlank;

import java.nio.file.Path;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.tools.iosource.InputResource;

public class FileHasContent
  extends BaseMatcher<InputResource>
{

  public static InputResource classpathResource(final String classpathResource)
  {
    try
    {
      requireNonNull(classpathResource, "No classpath resource provided");
      return new ClasspathInputResource("/" + classpathResource);
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public static InputResource fileResource(final TestOutputCapture testoutput)
  {
    requireNonNull(testoutput, "No test output cature provided");
    final Path filePath = testoutput.getFilePath();
    try
    {
      return FileInputResource.allowEmptyFileInputResource(filePath);
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  @Factory
  public static Matcher<InputResource> hasNoContent()
  {
    return new FileHasContent(null, null);
  }

  @Factory
  public static Matcher<InputResource> hasSameContentAndTypeAs(final InputResource classpathInputResource,
                                                               final String outputFormatValue)
  {
    if (classpathInputResource == null
        || !(classpathInputResource instanceof ClasspathInputResource))
    {
      throw new RuntimeException("No classpath resource to match with");
    }
    if (isBlank(outputFormatValue))
    {
      throw new RuntimeException("No output format provided");
    }
    return new FileHasContent(classpathInputResource, outputFormatValue);
  }

  @Factory
  public static Matcher<InputResource> hasSameContentAs(final InputResource classpathInputResource)
  {
    if (classpathInputResource == null
        || !(classpathInputResource instanceof ClasspathInputResource))
    {
      throw new RuntimeException("No file resource to match with");
    }
    return new FileHasContent(classpathInputResource, null);
  }

  private final InputResource referenceFileResource;
  private final String outputFormatValue;
  private List<String> failures;

  public FileHasContent(final InputResource referenceFileResource,
                        final String outputFormatValue)
  {
    this.referenceFileResource = referenceFileResource;
    this.outputFormatValue = outputFormatValue;
  }

  @Override
  public void describeMismatch(final Object item, final Description description)
  {
    // description.appendText("was ").appendValue(item);
    if (failures != null)
    {
      description.appendValueList("mismatched on:\n", "\n", "", failures);
    }
  }

  @Override
  public void describeTo(final Description description)
  {
    description.appendValue(referenceFileResource);
  }

  @Override
  public boolean matches(final Object actualValue)
  {
    try
    {
      // Clear failures from previous match
      failures = null;

      final String referenceFile = getReferenceFile();
      final Path file = getFilePath(actualValue);

      if (isBlank(referenceFile))
      {
        // Check if the file contents are empty
        return !exists(file) || size(file) == 0;
      }
      else
      {
        // Check file contents
        final String outputFormatValue = getNonNullOutputFormatValue();
        failures = compareOutput(referenceFile, file, outputFormatValue, false);
        return failures != null && failures.isEmpty();
      }
    }
    catch (final Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private Path getFilePath(final Object actualValue)
  {
    if (actualValue == null || !(actualValue instanceof FileInputResource))
    {
      throw new RuntimeException("No file input resource provided");
    }
    final Path file = ((FileInputResource) actualValue).getInputFile();
    return file;
  }

  private String getNonNullOutputFormatValue()
  {
    final String outputFormatValue;
    if (isBlank(this.outputFormatValue))
    {
      outputFormatValue = "text";
    }
    else
    {
      outputFormatValue = this.outputFormatValue;
    }
    return outputFormatValue;
  }

  private String getReferenceFile()
  {
    final String referenceFile;
    if (referenceFileResource == null)
    {
      referenceFile = null;
    }
    else
    {
      referenceFile = ((ClasspathInputResource) referenceFileResource)
        .getClasspathResource().substring(1);
    }
    return referenceFile;
  }

}
