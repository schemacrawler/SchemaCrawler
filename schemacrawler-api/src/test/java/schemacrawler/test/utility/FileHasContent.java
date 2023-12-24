/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static us.fatehi.utility.Utility.isBlank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FileHasContent extends BaseMatcher<TestResource> {

  public static TestResource classpathResource(final String classpathResource) {
    requireNonNull(classpathResource, "No classpath resource provided");
    return new TestResource("/" + classpathResource);
  }

  public static String contentsOf(final TestOutputCapture testoutput) {
    requireNonNull(testoutput, "No test output capture provided");
    return testoutput.getContents();
  }

  public static Matcher<TestResource> hasNoContent() {
    return new FileHasContent(null, null);
  }

  public static Matcher<TestResource> hasSameContentAndTypeAs(
      final TestResource classpathTestResource, final String outputFormatValue) {
    return hasSameContentAndTypeAs(classpathTestResource, outputFormatValue, true);
  }

  public static Matcher<TestResource> hasSameContentAs(final TestResource classpathTestResource) {
    return hasSameContentAndTypeAs(classpathTestResource, null, false);
  }

  public static Matcher<TestResource> hasSameContentAsClasspathResource(
      final String classpathTestResource) {
    return hasSameContentAndTypeAs(new TestResource(classpathTestResource), null, false);
  }

  public static TestResource outputOf(final Path filePath) {
    if (filePath == null) {
      return new TestResource();
    } else {
      return new TestResource(filePath);
    }
  }

  public static TestResource outputOf(final TestOutputCapture testoutput) {
    requireNonNull(testoutput, "No test output capture provided");
    final Path filePath = testoutput.getFilePath();
    return outputOf(filePath);
  }

  private static Matcher<TestResource> hasSameContentAndTypeAs(
      final TestResource classpathTestResource,
      final String outputFormatValue,
      final boolean validateOutputFormat) {
    if (classpathTestResource == null) {
      fail("No classpath resource to match with");
    }
    if (validateOutputFormat && isBlank(outputFormatValue)) {
      fail("No output format provided");
    }
    return new FileHasContent(classpathTestResource, outputFormatValue);
  }

  private final TestResource referenceFileResource;
  private final String outputFormatValue;
  private List<String> failures;

  private FileHasContent(final TestResource referenceFileResource, final String outputFormatValue) {
    this.referenceFileResource = referenceFileResource;
    this.outputFormatValue = outputFormatValue;
  }

  @Override
  public void describeMismatch(final Object item, final Description description) {
    // description.appendText("was ").appendValue(item);
    if (referenceFileResource == null) {
      String value;
      if (item instanceof TestResource) {
        try {
          final Path fileResource = ((TestResource) item).getFileResource().orElse(null);
          value = Files.lines(fileResource).limit(5).collect(Collectors.joining("\n"));
        } catch (final IOException e) {
          value = "<some output>";
        }
      } else {
        value = "<some output>";
      }
      description.appendText("was: " + value);
    } else if (failures != null) {
      description.appendText("mismatched on:\n" + String.join("\n", failures));
    }
  }

  @Override
  public void describeTo(final Description description) {
    if (referenceFileResource == null) {
      description.appendValue("no output");
    } else {
      description.appendValue(referenceFileResource);
    }
  }

  @Override
  public boolean matches(final Object actualValue) {
    try {
      // Clear failures from previous match
      failures = null;

      final String referenceFile = getReferenceFile();
      final Path file = getFilePath(actualValue);

      if (isBlank(referenceFile)) {
        // Check if the file contents are empty
        return !exists(file) || size(file) == 0;
      } else {
        // Check file contents
        final String outputFormatValue = getNonNullOutputFormatValue();
        failures = compareOutput(referenceFile, file, outputFormatValue, false);
        return failures != null && failures.isEmpty();
      }
    } catch (final Exception e) {
      return fail(e);
    }
  }

  private Path getFilePath(final Object actualValue) {
    if (actualValue == null || !(actualValue instanceof TestResource)) {
      throw new RuntimeException("No file input resource provided");
    }
    final Path file =
        ((TestResource) actualValue)
            .getFileResource()
            .orElseThrow(() -> new RuntimeException("No file input resource provided"));
    return file;
  }

  private String getNonNullOutputFormatValue() {
    final String outputFormatValue;
    if (isBlank(this.outputFormatValue)) {
      outputFormatValue = "text";
    } else {
      outputFormatValue = this.outputFormatValue;
    }
    return outputFormatValue;
  }

  private String getReferenceFile() {
    final String referenceFile;
    if (referenceFileResource == null) {
      referenceFile = null;
    } else {
      referenceFile =
          referenceFileResource
              .getClasspathResource()
              .map(resource -> resource.substring(1))
              .orElse(null);
    }
    return referenceFile;
  }
}
