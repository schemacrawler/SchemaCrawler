package schemacrawler.tools.options;

public interface LanguageType<T extends Enum<T>> {
  boolean matches(String languageName);
}
