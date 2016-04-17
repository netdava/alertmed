package alertmed.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    private Long id;
    private String name;
    private String location;
    private String medicine;
    private String email;
    private String phone;
    private String problem;
    private LocalDateTime submitTime;

}
