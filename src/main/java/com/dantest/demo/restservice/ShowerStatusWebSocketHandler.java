package com.dantest.demo.restservice;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ShowerStatusWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ShowerService showerService;

    public ShowerStatusWebSocketHandler(ShowerService showerService) {
        this.showerService = showerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Store session to send updates later
        sessions.add(session);
        sendAllShowersStatus(session);  // Send the current status to the newly connected client
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove session when connection is closed
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages from the client (if needed)
    }

    // Broadcast updates to all connected WebSocket clients
    public void broadcastShowersStatus(List<Shower> showers) throws IOException {
        String statusUpdate = showers.toString();
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(statusUpdate));
                } catch (IOException e) {
                    handleBrokenSession(session, e);
                }
            } else {
                sessions.remove(session);
            }
        }
    }

    private void sendAllShowersStatus(WebSocketSession session) throws IOException {
        if (session.isOpen()) {
            try {
                List<Shower> showers = showerService.getAllShowers();
                String statusUpdate = showers.toString();
                session.sendMessage(new TextMessage(statusUpdate));
            } catch (IOException e) {
                handleBrokenSession(session, e);
            }
        }
    }

    private void handleBrokenSession(WebSocketSession session, IOException e) {
        System.err.println("Error sending message to WebSocket client: " + e.getMessage());
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException closeException) {
            System.err.println("Error closing WebSocket session: " + closeException.getMessage());
        } finally {
            sessions.remove(session);
        }
    }
}



