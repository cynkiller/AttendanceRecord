package backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import backend.task.RehearsalTask;

@Service
@Configuration
@EnableScheduling
public class BackgroundService implements SchedulingConfigurer{

    @Autowired
    private RehearsalService rehearsalService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        /*
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
		executor.initialize();
		
		executor.execute(new BackgroundService("Set Next Rehearsal Information"));
        */
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.initialize();
        taskRegistrar.setScheduler(taskScheduler);
		//taskScheduler.schedule(new BackgroundService("Set Next Rehearsal Information"), new CronTrigger("0 13 * * 6 ?")); // Saturday 13:00
        //BackgroundService bs = new BackgroundService("Set Next Rehearsal Information");
        RehearsalTask rt = new RehearsalTask("RehearsalTask", rehearsalService, userInfoService);
        //taskScheduler.schedule(rt, new CronTrigger("0 13 * * * ?")); // Every day at 13:00
        taskRegistrar.addCronTask(rt, "0 0 * * * ?");
        rt.run();
    }
}