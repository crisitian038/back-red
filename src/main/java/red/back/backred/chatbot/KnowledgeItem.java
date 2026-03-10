package red.back.backred.chatbot;

import lombok.Data;
import java.util.List;

@Data
public class KnowledgeItem {
    private String titulo;
    private String intencion;          // Ej: "info_general", "requisitos", "costo", "horario"
    private List<String> entidades;     // Ej: ["bachillerato 286", "carreras ejecutivas"]
    private List<String> keywords;
    private String respuesta;
}