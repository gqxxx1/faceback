package com.sdjzu.faceattendancesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sdjzu.faceattendancesystem.mapper")
public class FaceAttendanceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceAttendanceSystemApplication.class, args);
    }

}
