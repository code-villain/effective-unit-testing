package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    boolean isSupervisor;
    LocalDateTime startDate;

    public LocalDateTime startingDate() {
        return startDate;
    }
}