package cn.javaer.snippets.box.jooq;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.JSONB;

/**
 * @author cn-src
 */
@Data
@AllArgsConstructor
public class Query {

    private String str1;

    @Contains
    private String str2;

    private JSONB jsonb1;

    @Contains
    private JSONB jsonb2;
}
