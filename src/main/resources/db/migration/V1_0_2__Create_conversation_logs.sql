-- Migration: Create conversation_logs table for chatbot learning
-- Date: 2026-03-06
-- Purpose: Store conversation history for analytics and continuous learning

CREATE TABLE IF NOT EXISTS conversation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_message VARCHAR(1000) NOT NULL,
    bot_response TEXT NOT NULL,
    detected_intent VARCHAR(100),
    detected_entity VARCHAR(200),
    confidence_score DECIMAL(3,2),
    user_feedback INT COMMENT '1 = útil, -1 = no útil, 0 = neutral',
    session_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_session_id (session_id),
    INDEX idx_intent (detected_intent),
    INDEX idx_feedback (user_feedback),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create view for conversation statistics
CREATE OR REPLACE VIEW conversation_stats AS
SELECT 
    DATE(created_at) as date,
    COUNT(*) as total_messages,
    SUM(CASE WHEN user_feedback = 1 THEN 1 ELSE 0 END) as positive_feedback,
    SUM(CASE WHEN user_feedback = -1 THEN 1 ELSE 0 END) as negative_feedback,
    AVG(confidence_score) as avg_confidence,
    COUNT(DISTINCT detected_intent) as unique_intents
FROM conversation_logs
GROUP BY DATE(created_at)
ORDER BY date DESC;

-- Create view for most common intents
CREATE OR REPLACE VIEW intent_ranking AS
SELECT 
    detected_intent,
    COUNT(*) as frequency,
    AVG(confidence_score) as avg_confidence,
    SUM(CASE WHEN user_feedback = 1 THEN 1 ELSE 0 END) as positive_count,
    SUM(CASE WHEN user_feedback = -1 THEN 1 ELSE 0 END) as negative_count
FROM conversation_logs
WHERE detected_intent IS NOT NULL
GROUP BY detected_intent
ORDER BY frequency DESC;
