package cn.alumik.pldl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PldlApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PldlApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Hello World!");
    }
}
