package org.apex.dataverse.port.driver.config;

import org.apex.dataverse.port.driver.conn.pool.PortExchanger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Danny.Huo
 * @date 2023/3/14 19:03
 * @since 0.1.0
 */
@Configuration
public class ConnExchangerConfig {

    @Bean
    public PortExchanger connExchanger(ConnectionPoolConfig poolConfig) {

        return new PortExchanger(poolConfig);
    }
}
