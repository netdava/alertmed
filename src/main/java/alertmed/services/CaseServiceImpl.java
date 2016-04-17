package alertmed.services;

import alertmed.model.Case;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import io.vertx.core.json.Json;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Data
@Component
public class CaseServiceImpl implements CaseService {

    private List<Case> cases;

    @Override
    public List<Case> list() {
        if (cases == null) {
            cases = loadData("cases.json");
        }
        return cases;
    }

    public List<Case> loadData(@NonNull String resourcePath) {
        TypeReference<List<Case>> CASE_LIST_TYPE = new TypeReference<List<Case>>() {
        };

        URL matchingJson = Resources.getResource(resourcePath);
        CharSource content = Resources.asCharSource(matchingJson, StandardCharsets.UTF_8);
        try {
            return Json.mapper.readValue(content.read(), CASE_LIST_TYPE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data.");
        }
    }


}
