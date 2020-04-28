package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.JSONB;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cn-src
 */
public abstract class JsonbConverters {
    private static final List<Converter<?, ?>> CONVERTERS = new ArrayList<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        JsonbConverters.CONVERTERS.add(ToJsonbConverter.INSTANCE);
        JsonbConverters.CONVERTERS.add(JsonbToConverter.INSTANCE);
        JsonbConverters.CONVERTERS.add(ToJsonNodeConverter.INSTANCE);
        JsonbConverters.CONVERTERS.add(JsonNodeToConverter.INSTANCE);
    }

    public static List<Converter<?, ?>> getConvertersToRegister() {
        return Collections.unmodifiableList(JsonbConverters.CONVERTERS);
    }

    @ReadingConverter
    public enum ToJsonbConverter implements Converter<PGobject, JSONB> {

        /**
         * 单实例.
         */
        INSTANCE;

        @Override
        public JSONB convert(final PGobject source) {
            return JSONB.valueOf(source.getValue());
        }
    }

    @WritingConverter
    public enum JsonbToConverter implements Converter<JSONB, String> {

        /**
         * 单实例.
         */
        INSTANCE;

        @Override
        public String convert(final JSONB source) {
            return source.data();
        }
    }

    @ReadingConverter
    public enum ToJsonNodeConverter implements Converter<PGobject, JsonNode> {

        /**
         * 单实例.
         */
        INSTANCE;

        @Override
        public JsonNode convert(final PGobject source) {
            try {
                return objectMapper.readTree(source.getValue());
            }
            catch (final JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @WritingConverter
    public enum JsonNodeToConverter implements Converter<JsonNode, String> {

        /**
         * 单实例.
         */
        INSTANCE;

        @Override
        public String convert(final JsonNode source) {
            try {
                return objectMapper.writeValueAsString(source);
            }
            catch (final JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
