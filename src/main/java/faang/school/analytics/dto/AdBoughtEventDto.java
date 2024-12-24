package faang.school.analytics.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AdBoughtEventDto(
        long postId,
        long userId,
        BigDecimal paymentAmount,
        int adDuration,
        LocalDateTime boughtAt
) {
}