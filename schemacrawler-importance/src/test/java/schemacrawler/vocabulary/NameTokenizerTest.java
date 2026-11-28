package schemacrawler.vocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.jupiter.api.Test;

public class NameTokenizerTest {

  private static List<String> stems(final String name) {
    return NameTokenizer.tokenize(name).tokens().stream().map(Token::stem).toList();
  }

  @Test
  public void underscoreAndCamelCase() {
    assertThat(stems("customer_order"), contains("customer", "order"));
    assertThat(stems("shippingAddressLine"), contains("shipping", "address", "line"));
  }

  @Test
  public void acronymAndSpacedAndHyphenated() {
    assertThat(stems("URLId"), empty()); // "url" and "id" are too short
    assertThat(stems("Order Details"), contains("order", "detail"));
    assertThat(stems("kebab-name"), contains("kebab", "name"));
  }

  @Test
  public void caseInsensitive() {
    assertThat(stems("ORDERS"), contains("order"));
    assertThat(stems("Orders"), contains("order"));
    assertThat(stems("orders"), contains("order"));
  }

  @Test
  public void digitsSeparateWords() {
    assertThat(stems("address1"), contains("address"));
    assertThat(stems("abcd1efgh"), contains("abcd", "efgh"));
    assertThat(stems("kna1"), empty());
  }

  @Test
  public void punctuationSeparatesWords() {
    assertThat(stems("ORDER#"), contains("order"));
    assertThat(stems("SYS$X"), empty());
    assertThat(stems("SYS_C0012345"), empty());
  }

  @Test
  public void shortTokens() {
    assertThat(stems("cust_no"), contains("cust"));
  }

  @Test
  public void singularWordsEndingInS() {
    assertThat(stems("status"), contains("status"));
    assertThat(stems("address"), contains("address"));
  }

  @Test
  public void wordIsLowercaseAndStemIsSingular() {
    final List<Token> tokens = NameTokenizer.tokenize("ORDERS").tokens();
    assertThat(tokens.get(0).word(), is("orders"));
    assertThat(tokens.get(0).stem(), is("order"));
  }

  @Test
  public void polishNamesWithAsciiBoundaries() {
    assertThat(stems("numerTelefonu"), contains("numer", "telefonu"));
    assertThat(stems("numer_telefonu"), contains("numer", "telefonu"));
    assertThat(stems("przedszkole_id"), contains("przedszkole"));
  }

  @Test
  public void combiningMarksStayInWord() {
    // "e" followed by a combining acute accent
    // The word keeps the combining mark, and the stem is normalized to the composed form
    final Token token = NameTokenizer.tokenize("café_name").tokens().get(0);
    assertThat(token.word(), is("café"));
    assertThat(token.stem(), is("café"));
    assertThat(stems("café_name"), contains("café", "name"));
  }

  @Test
  public void segmentCount() {
    assertThat(NameTokenizer.tokenize("customer").segmentCount(), is(1));
    assertThat(NameTokenizer.tokenize("address1").segmentCount(), is(1));
    assertThat(NameTokenizer.tokenize("customer_id").segmentCount(), is(2));
    assertThat(NameTokenizer.tokenize("shippingAddressLine").segmentCount(), is(3));
  }

  @Test
  public void tokenSegments() {
    final List<Token> tokens = NameTokenizer.tokenize("customer_order").tokens();
    assertThat(tokens.get(0).segment(), is(0));
    assertThat(tokens.get(1).segment(), is(1));
    final List<Token> glued = NameTokenizer.tokenize("abcd1efgh").tokens();
    assertThat(glued.get(0).segment(), is(0));
    assertThat(glued.get(1).segment(), is(0));
    // The short first segment is dropped, so the first token comes from segment 1
    assertThat(NameTokenizer.tokenize("no_cust").tokens().get(0).segment(), is(1));
  }

  @Test
  public void firstSegment() {
    final List<Token> tokens = NameTokenizer.tokenize("customer_order").tokens();
    assertThat(tokens.get(0).isFirstSegment(), is(true));
    assertThat(tokens.get(1).isFirstSegment(), is(false));
    // Digits split a word but not the segment
    final List<Token> glued = NameTokenizer.tokenize("abcd1efgh").tokens();
    assertThat(glued.get(0).isFirstSegment(), is(true));
    assertThat(glued.get(1).isFirstSegment(), is(true));
    // The short first segment is dropped, so no token is in the first segment
    assertThat(NameTokenizer.tokenize("no_cust").tokens().get(0).isFirstSegment(), is(false));
  }

  @Test
  public void blankNames() {
    final Tokenization tokenization = NameTokenizer.tokenize("  ");
    assertThat(tokenization.segmentCount(), is(0));
    assertThat(tokenization.tokens(), empty());
    assertThat(NameTokenizer.tokenize(null).segmentCount(), is(0));
  }

  @Test
  public void fullWidthLatinSharesStemWithAscii() {
    final Token fullWidth = NameTokenizer.tokenize("ｏｒｄｅｒ_detail").tokens().get(0);
    assertThat(fullWidth.stem(), is("order"));
    assertThat(fullWidth.word(), is("ｏｒｄｅｒ"));
    assertThat(stems("order_id"), contains("order"));
  }

  @Test
  public void halfWidthKatakanaSharesStemWithFullWidth() {
    final String halfWidth = "ｼﾞｭﾁｭｳ_detail";
    final String fullWidth = "ジュチュウ_id";
    assertThat(stems(halfWidth).get(0), is("ジュチュウ"));
    assertThat(stems(fullWidth), contains("ジュチュウ"));
  }

  @Test
  public void minimumLengthUsesNormalizedForm() {
    // Six code points as written, three after normalization
    assertThat(stems("ｶﾞｷﾞｸﾞ_id"), empty());
    assertThat(stems("ガギグ_id"), empty());
  }

  @Test
  public void ideographicSpaceSeparatesSegments() {
    final Tokenization tokenization = NameTokenizer.tokenize("customer　order");
    assertThat(tokenization.segmentCount(), is(2));
    assertThat(tokenization.tokens().size(), is(2));
  }
}
