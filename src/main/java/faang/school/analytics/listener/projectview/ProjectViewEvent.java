package faang.school.analytics.listener.projectview;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectViewEvent {
    @Positive
    @JsonProperty("projectId")
    private long projectId;
    @Positive
    @JsonProperty("userId")
    private long userId;
    @NotNull
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
