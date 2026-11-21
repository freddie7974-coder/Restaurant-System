package com.example.server.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionManager {
    private final Map<String, String> sessionToTable      = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToClientType = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> tableToSessions = new ConcurrentHashMap<>();
    private final Set<String> kitchenSessions             = ConcurrentHashMap.newKeySet();
    private final Set<String> waiterSessions              = ConcurrentHashMap.newKeySet();

    public void registerSession(String sessionId, String tableNumber, String clientType) {
        sessionToTable.put(sessionId, tableNumber);
        sessionToClientType.put(sessionId, clientType != null ? clientType : "TABLET");

        if ("KITCHEN".equalsIgnoreCase(clientType)) {
            kitchenSessions.add(sessionId);
        } else if ("WAITER".equalsIgnoreCase(clientType)) {
            waiterSessions.add(sessionId);
        } else {
            tableToSessions
                .computeIfAbsent(tableNumber, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);
        }
    }

    public void removeSession(String sessionId) {
        String tableNumber = sessionToTable.remove(sessionId);
        sessionToClientType.remove(sessionId);
        kitchenSessions.remove(sessionId);
        waiterSessions.remove(sessionId);

        if (tableNumber != null) {
            Set<String> sessions = tableToSessions.get(tableNumber);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) tableToSessions.remove(tableNumber);
            }
        }
    }

    public Set<String> getSessionsForTable(String tableNumber) {
        return tableToSessions.getOrDefault(tableNumber, Collections.emptySet());
    }
}
