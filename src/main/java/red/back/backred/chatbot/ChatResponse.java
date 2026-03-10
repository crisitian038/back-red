package red.back.backred.chatbot;

import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private String response;
    private Double confidenceScore;
    private String detectedIntent;
    private String detectedEntity;
    private List<String> suggestedQuestions;
    private Boolean isLearning; // indica si el sistema está aprendiendo
    
    public ChatResponse(String response, Double confidenceScore) {
        this.response = response;
        this.confidenceScore = confidenceScore;
        this.isLearning = false;
    }
}
