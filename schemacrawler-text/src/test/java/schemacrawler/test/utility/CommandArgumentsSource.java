package schemacrawler.test.utility;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class CommandArgumentsSource implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return Stream.of(Arguments.of("list", 1), Arguments.of("schema", 2));
  }
}
