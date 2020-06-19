package cn.javaer.snippets.box.model.pojo;

/**
 * {@link Product2} specific assertions - Generated by CustomAssertionGenerator.
 *
 * Although this class is not final to allow Soft assertions proxy, if you wish to extend it, 
 * extend {@link AbstractProduct2Assert} instead.
 */
@javax.annotation.Generated(value="assertj-assertions-generator")
public class Product2Assert extends AbstractProduct2Assert<Product2Assert, Product2> {

  /**
   * Creates a new <code>{@link Product2Assert}</code> to make assertions on actual Product2.
   * @param actual the Product2 we want to make assertions on.
   */
  public Product2Assert(Product2 actual) {
    super(actual, Product2Assert.class);
  }

  /**
   * An entry point for Product2Assert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one can write directly: <code>assertThat(myProduct2)</code> and get specific assertion with code completion.
   * @param actual the Product2 we want to make assertions on.
   * @return a new <code>{@link Product2Assert}</code>
   */
  @org.assertj.core.util.CheckReturnValue
  public static Product2Assert assertThat(Product2 actual) {
    return new Product2Assert(actual);
  }
}
