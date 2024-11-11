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

package schemacrawler.test.utility;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.size;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

public class FileHasContent extends BaseMatcher<TestResource> {

  public static TestResource classpathResource(final String classpathResource) {
    requireNotBlank(classpathResource, "No classpath resource provided");
    return TestResource.fromClasspath(classpathResource);
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

  public static TestResource outputOf(final Path filePath) {
    return TestResource.fromFilePath(filePath);
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

  private final TestResource expectedResource;
  private final String outputFormatValue;
  private List<String> failures;

  private FileHasContent(final TestResource expectedResource, final String outputFormatValue) {
    if (expectedResource != null) {
      this.expectedResource = expectedResource;
    } else {
      this.expectedResource = TestResource.empty();
    }

    if (isBlank(outputFormatValue)) {
      this.outputFormatValue = "text";
    } else {
      this.outputFormatValue = outputFormatValue;
    }
  }

  @Override
  public void describeMismatch(final Object item, final Description description) {
    // description.appendText("was ").appendValue(item);
    if (!expectedResource.hasResourceString()) {
      String value;
      if (item instanceof TestResource) {
        try {
          final TestResource testResource = (TestResource) item;
          if (!testResource.isAvailable()) {
            throw new IOException(
                String.format("Expected output file <%s> is not available", testResource));
          }
          final Path fileResource = Paths.get(testResource.getResourceString());
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
    if (!expectedResource.hasResourceString()) {
      description.appendValue("no output");
    } else {
      description.appendValue(expectedResource);
    }
  }

  @Override
  public boolean matches(final Object actualValue) {
    try {
      // Clear failures from previous match
      failures = null;

      final Path file = getFilePath(actualValue);

      // If there is no expected classpath resource, also make sure that
      // the output has no contents
      if (!expectedResource.hasResourceString()) {
        // Check if the file contents are empty
        final boolean hasNoFileContents = !exists(file) || size(file) == 0;
        return hasNoFileContents;
      }

      // Check file contents
      final String referenceFile = expectedResource.getResourceString();
      failures = compareOutput(referenceFile, file, outputFormatValue);
      return failures != null && failures.isEmpty();
    } catch (final Exception e) {
      return fail(e);
    }
  }

  private Path getFilePath(final Object actualValue) {
    if (actualValue == null || !(actualValue instanceof TestResource)) {
      throw new RuntimeException("No file input resource provided");
    }
    final TestResource testResource = (TestResource) actualValue;
    final Path file = Paths.get(testResource.getResourceString());
    return file;
  }
}
