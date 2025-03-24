package com.example.finalproject.handler;


import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

// 실제로 웹소켓을 통해 위치정보를 처리할 핸들러 구현.
// 클라이언트로부터 메시지를 받아 처리하고, 배달원의 실시간 위치를 클라이언트에게 전송하는 역할
public class LocationWebSockethandler extends TextWebSocketHandler {

    // 이 핸들러는 클라이언트로부터 데이터를 받아 처리하고, 실시간으로 메시지를 보낸다.
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload(); // 여기에 위치정보를 처리하는 로직을 추가
        // 클라이언트에서 배달원 ID를 받고, 해당 배달원의 현재 위치를 반환

        System.out.println("응답메시지: " + payload);

        // 예시로 받은 메시지에 "현재위치" 택스트를 포함해 응답 보내기
        String response = "현재 위치 ex 위도 31.5565 경도 126.978";
        session.sendMessage(new TextMessage(response));
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        super.afterConnectionEstablished(session);
        System.out.println("웹소켓 연결 완료");
    }
}
