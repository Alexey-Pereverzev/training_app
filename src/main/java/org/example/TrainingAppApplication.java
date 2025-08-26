package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableDiscoveryClient
public class TrainingAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingAppApplication.class, args);
    }
}