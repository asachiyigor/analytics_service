package faang.school.analytics.listener.projectview;

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
    private long projectId;
    private long userId;
    private LocalDateTime createdAt;
}
