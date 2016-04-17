package alertmed.services;

import alertmed.model.Alert;

import java.util.List;

public interface AlertsService {

    List<Alert> list();

    void addAlert(Alert alert);

    long size();

}
