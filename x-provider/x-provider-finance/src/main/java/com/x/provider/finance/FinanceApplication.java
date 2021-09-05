package com.x.provider.finance;

import com.x.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;


@EnableKafka
@EnableCustomSwagger2
@SpringCloudApplication
@EnableFeignClients(basePackages="com.x.provider.api")
@MapperScan("com.x.provider.finance.mapper")
public class FinanceApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(FinanceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ finance provider started success   ლ(´ڡ`ლ)ﾞ  \n" +
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
