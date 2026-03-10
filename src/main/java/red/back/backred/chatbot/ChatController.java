package red.back.backred.chatbot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final AdvancedChatService advancedChatService;

    @Autowired(required = false)
    private ConversationLogRepository conversationLogRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> body, HttpSession httpSession) {
        String message = body.get("message");
        String useAdvanced = body.getOrDefault("advanced", "true");

        // Obtener o crear ID de sesión
        String sessionId = (String) httpSession.getAttribute("sessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            httpSession.setAttribute("sessionId", sessionId);
        }

        // Recuperar contexto actual de la sesión HTTP
        String ultimoTema = (String) httpSession.getAttribute("ultimoTema");
        String ultimaIntencion = (String) httpSession.getAttribute("ultimaIntencion");
        String ultimaEntidad = (String) httpSession.getAttribute("ultimaEntidad");

        Map<String, Object> sessionMap = new HashMap<>();
        if (ultimoTema != null) sessionMap.put("ultimoTema", ultimoTema);
        if (ultimaIntencion != null) sessionMap.put("ultimaIntencion", ultimaIntencion);
        if (ultimaEntidad != null) sessionMap.put("ultimaEntidad", ultimaEntidad);

        // Usar servicio avanzado si está disponible
        ChatResponse chatResponse;
        if ("true".equals(useAdvanced)) {
            chatResponse = advancedChatService.obtenerRespuestaAvanzada(message, sessionMap, sessionId);
        } else {
            String response = chatService.getResponse(message, sessionMap);
            chatResponse = new ChatResponse(response, 0.7);
        }

        // Guardar nuevo contexto en la sesión HTTP
        if (sessionMap.containsKey("ultimoTema")) {
            httpSession.setAttribute("ultimoTema", sessionMap.get("ultimoTema"));
        }
        if (sessionMap.containsKey("ultimaIntencion")) {
            httpSession.setAttribute("ultimaIntencion", sessionMap.get("ultimaIntencion"));
        }
        if (sessionMap.containsKey("ultimaEntidad")) {
            httpSession.setAttribute("ultimaEntidad", sessionMap.get("ultimaEntidad"));
        }

        // Construir respuesta completa
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("response", chatResponse.getResponse());
        responseMap.put("confidence", chatResponse.getConfidenceScore());
        responseMap.put("intent", chatResponse.getDetectedIntent());
        responseMap.put("entity", chatResponse.getDetectedEntity());
        responseMap.put("suggestions", chatResponse.getSuggestedQuestions());

        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, String>> sendFeedback(
            @RequestBody Map<String, Object> body,
            HttpSession httpSession) {

        try {
            String message = (String) body.get("message");
            Integer feedback = ((Number) body.get("feedback")).intValue(); // 1 = útil, -1 = no útil

            if (conversationLogRepository == null) {
                return ResponseEntity.ok(Map.of("status", "feedback received (database not available)"));
            }

            String sessionId = (String) httpSession.getAttribute("sessionId");
            var logs = conversationLogRepository.findBySessionId(sessionId);

            if (!logs.isEmpty()) {
                ConversationLog lastLog = logs.get(logs.size() - 1);
                lastLog.setUserFeedback(feedback);
                conversationLogRepository.save(lastLog);
            }

            return ResponseEntity.ok(Map.of("status", "feedback recorded successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getChatStats(HttpSession httpSession) {
        try {
            if (conversationLogRepository == null) {
                return ResponseEntity.ok(Map.of("message", "Statistics not available"));
            }

            String sessionId = (String) httpSession.getAttribute("sessionId");
            var allConversations = conversationLogRepository.findBySessionId(sessionId);
            var positiveResponses = conversationLogRepository.findByUserFeedback(1);
            var negativeResponses = conversationLogRepository.findByUserFeedback(-1);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalMessages", allConversations.size());
            stats.put("positiveResponses", positiveResponses.size());
            stats.put("negativeResponses", negativeResponses.size());
            stats.put("satisfactionRate",
                allConversations.isEmpty() ? 0 :
                (double) positiveResponses.size() / allConversations.size() * 100);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}