package com.acore.report.config;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 端点 — 推送审查进度
 *
 * @author acore
 */
@Slf4j
@Component
@ServerEndpoint("/ws/review/{pullRequestId}")
public class ReviewWebSocketEndpoint {

    /** 连接池：pullRequestId -> Session */
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("pullRequestId") String pullRequestId) {
        SESSION_MAP.put(pullRequestId, session);
        log.info("WebSocket 连接建立: pullRequestId={}, sessionId={}", pullRequestId, session.getId());
        sendMessage(session, String.format("{\"type\":\"connected\",\"pullRequestId\":\"%s\"}", pullRequestId));
    }

    @OnClose
    public void onClose(Session session, @PathParam("pullRequestId") String pullRequestId) {
        SESSION_MAP.remove(pullRequestId);
        log.info("WebSocket 连接关闭: pullRequestId={}", pullRequestId);
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("pullRequestId") String pullRequestId) {
        log.error("WebSocket 错误: pullRequestId={}, error={}", pullRequestId, error.getMessage());
        SESSION_MAP.remove(pullRequestId);
    }

    /**
     * 向指定 PR 的客户端推送消息
     */
    public static void pushProgress(Long pullRequestId, String stage, int progress, String message) {
        String key = String.valueOf(pullRequestId);
        Session session = SESSION_MAP.get(key);
        if (session != null && session.isOpen()) {
            String json = String.format(
                    "{\"type\":\"progress\",\"stage\":\"%s\",\"progress\":%d,\"message\":\"%s\"}",
                    stage, progress, message);
            sendMessage(session, json);
        }
    }

    /**
     * 推送审查完成消息
     */
    public static void pushCompleted(Long pullRequestId, Long reportId) {
        String key = String.valueOf(pullRequestId);
        Session session = SESSION_MAP.get(key);
        if (session != null && session.isOpen()) {
            String json = String.format(
                    "{\"type\":\"completed\",\"pullRequestId\":%d,\"reportId\":%d}",
                    pullRequestId, reportId);
            sendMessage(session, json);
        }
    }

    private static void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("WebSocket 发送消息失败: {}", e.getMessage());
        }
    }
}