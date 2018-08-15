
package backend.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import backend.service.BackgroundService;;

@SpringBootApplication(scanBasePackages = { "backend.*" })
@EnableMongoRepositories("backend.repo")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		/*
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
		executor.initialize();
		
		executor.execute(new BackgroundService("Set Next Rehearsal Information"));
		*/

		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.initialize();
		//taskScheduler.schedule(new BackgroundService("Set Next Rehearsal Information"), new CronTrigger("0 13 * * 6 ?")); // Saturday 13:00
		taskScheduler.schedule(new BackgroundService("Set Next Rehearsal Information"), new CronTrigger("0 13 * * * ?")); // Every day at 13:00
	}
}
