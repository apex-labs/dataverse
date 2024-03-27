package org.apex.dataverse.port;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Danny.Huo
 * @date 2023/5/11 14:04
 * @since 0.1.0
 */
@SpringBootApplication(scanBasePackages = {"org.apex.dataverse.port"})
public class PortApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortApplication.class, args);
    }
}
