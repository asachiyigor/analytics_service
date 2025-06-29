package faang.school.analytics.client;

import faang.school.analytics.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {
    @GetMapping("api/v1/users/{userId}")
    UserDto getUser(@PathVariable long userId);
}
