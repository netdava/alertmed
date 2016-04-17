package alertmed;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.json.Json;

public class JacksonConfig {
        public static void configure() {
        Json.mapper.registerModule(new JavaTimeModule());
        Json.mapper.registerModule(new Jdk8Module());
        Json.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Json.mapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        Json.prettyMapper.registerModule(new JavaTimeModule());
        Json.prettyMapper.registerModule(new Jdk8Module());
        Json.prettyMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Json.prettyMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
    }

}
