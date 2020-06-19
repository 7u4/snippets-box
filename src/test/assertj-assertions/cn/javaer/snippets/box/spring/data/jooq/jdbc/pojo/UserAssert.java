package cn.javaer.snippets.box.spring.data.jooq.jdbc.pojo;

/**
 * {@link User} specific assertions - Generated by CustomAssertionGenerator.
 *
 * Although this class is not final to allow Soft assertions proxy, if you wish to extend it, 
 * extend {@link AbstractUserAssert} instead.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class UserAssert extends AbstractUserAssert<UserAssert, User> {

  /**
   * Creates a new <code>{@link UserAssert}</code> to make assertions on actual User.
   * @param actual the User we want to make assertions on.
   */
  public UserAssert(User actual) {
    super(actual, UserAssert.class);
  }

  /**
   * An entry point for UserAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myUser)</code> and get specific assertion with code completion.
   * @param actual the User we want to make assertions on.
   * @return a new <code>{@link UserAssert}</code>
   */
  @org.assertj.core.util.CheckReturnValue
  public static UserAssert assertThat(User actual) {
    return new UserAssert(actual);
  }
}
