package com.paascloud.provider.customer;

import com.paascloud.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;


@EnableCustomSwagger2
@SpringCloudApplication
@EnableFeignClients(basePackages="com.paascloud.provider.api")
@MapperScan("com.paascloud.provider.customer.mapper")
public class CustomerApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(CustomerApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ customer provider started success   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
