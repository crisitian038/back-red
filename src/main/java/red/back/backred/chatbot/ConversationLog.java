package red.back.backred.chatbot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")   // <-- Cambio aquí
    private String userMessage;

    @Column(columnDefinition = "TEXT")   // <-- Cambio aquí
    private String botResponse;

    @Column(name = "detected_intent")
    private String detectedIntent;

    @Column(name = "detected_entity")
    private String detectedEntity;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "user_feedback")
    private Integer userFeedback;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
