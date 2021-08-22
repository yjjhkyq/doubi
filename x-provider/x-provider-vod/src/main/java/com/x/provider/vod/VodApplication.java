package com.x.provider.vod;

import com.x.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableCustomSwagger2
@SpringCloudApplication
@EnableFeignClients(basePackages="com.x.provider.api")
@MapperScan("com.x.provider.vod.mapper")
public class VodApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(VodApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ vod provider started success   ლ(´ڡ`ლ)ﾞ  \n" +
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
