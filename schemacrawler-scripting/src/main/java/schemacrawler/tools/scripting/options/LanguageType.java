package schemacrawler.tools.scripting.options;

public interface LanguageType<T extends Enum<T>> {
  boolean matches(String languageName);
}
