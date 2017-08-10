package boot.sample.shima.chat.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import boot.sample.shima.chat.history.HistoryService;

@Component
public class ChannelHandler extends TextWebSocketHandler {

    @Autowired
    private HistoryService historyService;

    private ConcurrentHashMap<String, Set<WebSocketSession>> channelSessionPool = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String channelId = session.getUri().getQuery();

        channelSessionPool.compute(channelId, (key, sessions) -> {
            if (sessions == null) {
                sessions = new CopyOnWriteArraySet<>();
            }
            sessions.add(session);
            return sessions;
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String channelId = session.getUri().getQuery();
        for (WebSocketSession channelSession : channelSessionPool.get(channelId)) {
            channelSession.sendMessage(message);
        }
        // messageの中身を解析してヒストリーに追加
        @SuppressWarnings("unchecked")
        Map<String, String> msgMap = new ObjectMapper().readValue(message.getPayload(), HashMap.class);
        String userId = msgMap.get("user_id");
        if ("system".equals(userId)) {
            return;
        }
        String userName = msgMap.get("user_name");
        String messageText = msgMap.get("message");
        historyService.registMessageHistory(channelId, userId, userName, messageText);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String channelId = session.getUri().getQuery();
        channelSessionPool.compute(channelId, (key, sessions) -> {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessions = null;
            }
            return sessions;
        });
    }
}
