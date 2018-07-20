package boot.sample.shima.chat.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.service.ChannelService;
import boot.sample.shima.chat.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import boot.sample.shima.chat.service.HistoryService;

@Component
public class ChannelHandler extends TextWebSocketHandler {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChatUserService userService;

    private Map<String, Set<WebSocketSession>> channelSessionPool = new ConcurrentHashMap<>();

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
        // クエリ文字列からチャンネルIDを取得
        String channelId = session.getUri().getQuery();
        // 送信者情報を取得
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, String> msgMap = mapper.readValue(message.getPayload(), HashMap.class);
        String type = msgMap.get("type");
        String userId = msgMap.get("user_id");

        if (!"system-message".equals(type) && !"force-close".equals(type)) {
            // messageの中身をヒストリーに追加
            String userName = msgMap.get("user_name");
            String messageText = msgMap.get("message");
            historyService.resistMessageHistory(channelId, userId, userName, messageText, type);
            // 送信メッセージにユーザー画像を追加
            ChatUser user = userService.loadUserByUserId(userId);
            msgMap.put("user_image", user.userImageBase64());
            // メッセージ送信時に最終ログイン日時を更新
            channelService.lastLogin(channelId, userId);
        }

        TextMessage msg = new TextMessage(mapper.writeValueAsBytes(msgMap));
        for (WebSocketSession channelSession : channelSessionPool.get(channelId)) {
            channelSession.sendMessage(msg);
        }
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
