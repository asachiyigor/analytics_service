package faang.school.analytics;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("faang.school.analytics.client")
@ConfigurationPropertiesScan
public class AnalyticsServiceApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AnalyticsServiceApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}