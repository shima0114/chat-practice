package boot.sample.shima.chat.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import boot.sample.shima.chat.entity.AttachmentFile;
import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import boot.sample.shima.chat.service.HistoryService;

import javax.websocket.Decoder;

@Component
public class ChannelHandler extends TextWebSocketHandler {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ChannelService channelService;

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
        String channelId = session.getUri().getQuery();
        for (WebSocketSession channelSession : channelSessionPool.get(channelId)) {
            channelSession.sendMessage(message);
        }
        // messageの中身を解析してヒストリーに追加
        @SuppressWarnings("unchecked")
        Map<String, String> msgMap = new ObjectMapper().readValue(message.getPayload(), HashMap.class);
        String userId = msgMap.get("user_id");
        String type = msgMap.get("type");
        if ("system-message".equals(type) || "force-close".equals(type)) {
            return;
        }
        String userName = msgMap.get("user_name");
        String messageText = msgMap.get("message");
        historyService.registMessageHistory(channelId, userId, userName, messageText, type);
        // メッセージ送信時に最終ログイン日時を更新
        channelService.lastLogin(channelId, userId);
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
