package com.example.server.config;

import com.example.server.service.WebSocketSessionManager;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final WebSocketSessionManager sessionManager;

    public WebSocketEventListener(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId   = accessor.getSessionId();
        String tableNumber = accessor.getFirstNativeHeader("tableNumber");
        String clientType  = accessor.getFirstNativeHeader("clientType");

        System.out.println("WebSocket connected: sessionId=" + sessionId
                + "  tableNumber=" + tableNumber + "  clientType=" + clientType);

        if (tableNumber != null) {
            sessionManager.registerSession(sessionId, tableNumber, clientType);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        System.out.println("WebSocket disconnected: sessionId=" + sessionId);
        sessionManager.removeSession(sessionId);
    }
}
