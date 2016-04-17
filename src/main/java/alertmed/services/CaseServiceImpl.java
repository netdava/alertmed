package alertmed.services;

import alertmed.model.Alert;
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
public class CaseServiceImpl implements AlertsService {

    private List<Alert> alerts;

    @Override
    public List<Alert> list() {
        if (alerts == null) {
            alerts = loadData("cases.json");
        }
        return alerts;
    }

    @Override
    public void addAlert(@NonNull Alert alert) {
        alerts.add(alert);
    }

    @Override
    public long size() {
        return alerts.size();
    }

    public List<Alert> loadData(@NonNull String resourcePath) {
        TypeReference<List<Alert>> CASE_LIST_TYPE = new TypeReference<List<Alert>>() {
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
