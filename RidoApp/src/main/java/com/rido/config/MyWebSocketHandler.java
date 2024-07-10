package com.rido.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

	
	private static Set<WebSocketSession> connectedSessions = Collections.newSetFromMap(new ConcurrentHashMap<WebSocketSession, Boolean>());
	private final Logger log = Logger.getLogger(MyWebSocketHandler.class.getName());

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.info("New connection established: " + session.getId() );
		   connectedSessions.add(session);
	}

	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		log.info("Received message: " + message.getPayload() + " for session: " + session.getId());
		
	}

	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		
		log.info("Connection closed: " + session.getId() );
		
		connectedSessions.remove(session);
	}



	public void broadcastMessage(String message) {
		log.info("Broadcasting message: " + message);
		for (WebSocketSession session : connectedSessions) {
			try {
				if (session.isOpen()) {
					session.sendMessage(new TextMessage(message));
					log.info("Message sent to session: " + session.getId());
				}
			} catch (IOException e) {
				log.warning("Error sending broadcast message: " + e.getMessage());
			}
		}
	}
	
}
