package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.datasource.SingleUseUserCredentials;
import us.fatehi.utility.datasource.UserCredentials;

public class SingleUseUserCredentialsTest {

  private final String user = "usr098";
  private final String password = "pwd123";

  @Test
  public void noneSet() throws Exception {
    final UserCredentials credentials1 = new SingleUseUserCredentials();
    assertThat(credentials1.hasUser(), is(false));
    assertThat(credentials1.hasPassword(), is(false));
    assertThat(credentials1.getUser(), is(nullValue()));
    assertThat(credentials1.getPassword(), is(nullValue()));
    assertThat(credentials1.toString(), is("UserCredentials [user=\"null\", password=\"*****\"]"));

    // Clear password should have no effect
    credentials1.clearPassword();

    assertThat(credentials1.hasPassword(), is(false));
    assertThrows(IllegalAccessError.class, () -> credentials1.getPassword());
  }

  @Test
  public void onlyPasswordSet() throws Exception {
    final UserCredentials credentials1 = new SingleUseUserCredentials(null, password);
    testOnlyPasswordSet(credentials1);

    final UserCredentials credentials2 = new SingleUseUserCredentials(null, password);
    testOnlyPasswordSet(credentials2);
  }

  @Test
  public void onlyUserSet() throws Exception {
    final UserCredentials credentials1 = new SingleUseUserCredentials(user, null);
    assertThat(credentials1.hasUser(), is(true));
    assertThat(credentials1.hasPassword(), is(false));
    assertThat(credentials1.getUser(), is(user));
    assertThat(credentials1.getPassword(), is(nullValue()));
    assertThat(
        credentials1.toString(), is("UserCredentials [user=\"" + user + "\", password=\"*****\"]"));

    // Clear password should have no effect
    credentials1.clearPassword();

    assertThat(credentials1.hasPassword(), is(false));
    assertThrows(IllegalAccessError.class, () -> credentials1.getPassword());
  }

  private void testOnlyPasswordSet(final UserCredentials credentials) {
    final String password = "pwd123";
    assertThat(credentials.hasUser(), is(false));
    assertThat(credentials.hasPassword(), is(true));
    assertThat(credentials.getUser(), is(nullValue()));
    assertThat(credentials.getPassword(), is(password));

    // Clear password should have no effect
    credentials.clearPassword();

    assertThat(credentials.hasPassword(), is(false));
    assertThrows(IllegalAccessError.class, () -> credentials.getPassword());
  }
}
