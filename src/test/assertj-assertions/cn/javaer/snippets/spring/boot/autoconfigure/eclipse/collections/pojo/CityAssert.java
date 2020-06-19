package cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.pojo;

/**
 * {@link City} specific assertions - Generated by CustomAssertionGenerator.
 *
 * Although this class is not final to allow Soft assertions proxy, if you wish to extend it, 
 * extend {@link AbstractCityAssert} instead.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class CityAssert extends AbstractCityAssert<CityAssert, City> {

  /**
   * Creates a new <code>{@link CityAssert}</code> to make assertions on actual City.
   * @param actual the City we want to make assertions on.
   */
  public CityAssert(City actual) {
    super(actual, CityAssert.class);
  }

  /**
   * An entry point for CityAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myCity)</code> and get specific assertion with code completion.
   * @param actual the City we want to make assertions on.
   * @return a new <code>{@link CityAssert}</code>
   */
  @org.assertj.core.util.CheckReturnValue
  public static CityAssert assertThat(City actual) {
    return new CityAssert(actual);
  }
}
