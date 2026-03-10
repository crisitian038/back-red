package red.back.backred.chatbot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationLogRepository extends JpaRepository<ConversationLog, Long> {
    List<ConversationLog> findBySessionId(String sessionId);
    List<ConversationLog> findByUserFeedback(Integer feedback);
    List<ConversationLog> findByDetectedIntentAndUserFeedback(String intent, Integer feedback);
}
