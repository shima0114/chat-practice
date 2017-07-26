package jp.co.commerce.sample.shima.chat;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import jp.co.commerce.sample.shima.chat.history.HistoryService;
import jp.co.commerce.sample.shima.chat.room.ChatRoomService;
import jp.co.commerce.sample.shima.chat.user.ChatUserService;

@SpringBootApplication
@EnableScheduling
public class ChatPracticeApplication implements SchedulingConfigurer {

    @Bean
    public ChatRoomService rooms() {
        return new ChatRoomService();
    }

    @Bean
    public HistoryService histories() {
        return new HistoryService();
    }

    @Bean
    public ChatUserService user() {
        return new ChatUserService();
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
