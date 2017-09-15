package boot.sample.shima.chat;

import java.util.concurrent.Executor;

import boot.sample.shima.chat.service.AttachmentFileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import boot.sample.shima.chat.service.ChannelService;
import boot.sample.shima.chat.service.HistoryService;
import boot.sample.shima.chat.service.ChatUserService;

@SpringBootApplication
@EnableScheduling
public class ChatPracticeApplication implements SchedulingConfigurer {

    @Bean
    public ChannelService rooms() {
        return new ChannelService();
    }

    @Bean
    public HistoryService histories() {
        return new HistoryService();
    }

    @Bean
    public ChatUserService user() {
        return new ChatUserService();
    }

    @Bean
    public AttachmentFileService fileService() {
        return new AttachmentFileService();
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatPracticeApplication.class, args);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

    @Bean(destroyMethod="shutdown")
    public Executor taskScheduler() {
        ThreadPoolTaskScheduler threadPoolScheduler = new ThreadPoolTaskScheduler();
        threadPoolScheduler.setPoolSize(10);
        threadPoolScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolScheduler.setAwaitTerminationSeconds(300);
        return threadPoolScheduler;
    }

}
