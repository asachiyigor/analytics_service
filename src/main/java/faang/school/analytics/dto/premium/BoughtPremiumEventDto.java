package faang.school.analytics.dto.premium;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BoughtPremiumEventDto(
    @Positive
    Long userId,

    @Positive
    BigDecimal sum,

    @NotNull
    int days,

    @NotNull
    String receivedAt
) {

}
