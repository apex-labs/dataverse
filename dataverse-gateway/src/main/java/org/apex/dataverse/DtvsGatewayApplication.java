package org.apex.dataverse;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @ClassName: DtvsGatewayApplication
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/13 15:03
 */
@SpringCloudApplication
public class DtvsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DtvsGatewayApplication.class,  args);
    }
}
