package boot.sample.shima.chat;

import java.util.concurrent.Executor;

import boot.sample.shima.chat.entity.ChatGroup;
import boot.sample.shima.chat.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableScheduling
public class ChatPracticeApplication extends WebMvcConfigurerAdapter implements SchedulingConfigurer {

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

    @Bean
    public ChatGroupService groupService() {
        return new ChatGroupService();
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatPracticeApplication.class, args);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
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
