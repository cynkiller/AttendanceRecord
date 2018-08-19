
package backend.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = { "backend.*" })
@EnableMongoRepositories("backend.repo")
public class Application {

	@PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+08:00"));   // It will set china timezone
        System.out.println("Spring boot application running in UTC timezone :"+new Date());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
