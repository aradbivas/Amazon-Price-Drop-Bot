package Product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication(scanBasePackages = {
        "com.pricedrop.amqp",
        "Product"
})
@EnableFeignClients(basePackages = {"clients.user", "clients.Bot"})
@EnableEurekaClient
@EnableAsync
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
    @Bean
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setKeepAliveSeconds(1);
        taskExecutor.setQueueCapacity(100);

        taskExecutor.setThreadNamePrefix("TaskExecutor");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
