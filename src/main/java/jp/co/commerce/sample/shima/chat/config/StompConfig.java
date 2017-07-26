package jp.co.commerce.sample.shima.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("chat");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // メッセージ処理Controllerのパス
        registry.setApplicationDestinationPrefixes("/send");
        // 購読セッションブローカー
        registry.enableSimpleBroker("/topic");
    }

}
