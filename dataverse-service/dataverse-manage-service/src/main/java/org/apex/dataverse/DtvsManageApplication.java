package org.apex.dataverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * dataverse manage start
 *
 */
@SpringBootApplication
@SpringCloudApplication
@EnableFeignClients(basePackages = {"org.apex.dataverse.feign"})
public class DtvsManageApplication
{
    public static void main( String[] args )
    {

        SpringApplication.run(DtvsManageApplication.class, args);
    }
}
