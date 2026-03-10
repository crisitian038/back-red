package red.back.backred.chatbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.SpanishStemmer;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ChatService {

    private List<KnowledgeItem> knowledge;

    @PostConstruct
    public void loadKnowledge() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = new ClassPathResource("knowledge.json").getInputStream();
            knowledge = mapper.readValue(input, new TypeReference<List<KnowledgeItem>>() {});
            System.out.println("Base de conocimiento cargada: " + knowledge.size());
        } catch (Exception e) {
            e.printStackTrace();
            knowledge = new ArrayList<>(); // evitar null pointer si falla la carga
        }

        // Validar que ninguna respuesta sea null o vacía
        for (KnowledgeItem item : knowledge) {
            if (item.getRespuesta() == null || item.getRespuesta().isBlank()) {
                item.setRespuesta("Información no disponible temporalmente. Por favor contacta a soporte.");
                System.out.println("Advertencia: Item con respuesta vacía: " + item.getTitulo());
            }
        }
    }

    // =========================
    // NORMALIZAR TEXTO (sin acentos, minúsculas)
    // =========================
    private String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase();
    }

    // =========================
    // STEMMING usando Snowball (Lucene)
    // =========================
    private String stem(String word) {
        SpanishStemmer stemmer = new SpanishStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    // =========================
    // DISTANCIA DE LEVENSHTEIN
    // =========================
    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }

    // =========================
    // DETECTAR INTENCIÓN
    // =========================
    private String detectarIntencion(String mensaje) {
        String clean = normalize(mensaje);
        if (clean.contains("requisito") || clean.contains("documento") || clean.contains("necesito") || clean.contains("inscripcion"))
            return "requisitos";
        if (clean.contains("costo") || clean.contains("precio") || clean.contains("colegiatura") || clean.contains("pago"))
            return "costo";
        if (clean.contains("horario") || clean.contains("hora") || clean.contains("abren") || clean.contains("atencion"))
            return "horario";
        if (clean.contains("ubicacion") || clean.contains("direccion") || clean.contains("mapa") || clean.contains("donde"))
            return "ubicacion";
        if (clean.contains("telefono") || clean.contains("llamar") || clean.contains("contacto"))
            return "telefono";
        if (clean.contains("correo") || clean.contains("email"))
            return "correo";
        if (clean.contains("que es") || clean.contains("informacion") || clean.contains("cuentame") || clean.contains("explica") || clean.contains("que ofrecen"))
            return "info_general";
        return null;
    }

    // =========================
    // DETECTAR ENTIDAD (programa)
    // =========================
    private String detectarEntidad(String mensaje) {
        String clean = normalize(mensaje);
        if (clean.contains("bachillerato 286") || clean.contains("acuerdo 286") || clean.contains("prepa 286") || (clean.contains("bachillerato") && clean.contains("286")))
            return "bachillerato 286";
        if (clean.contains("bachillerato 2") || clean.contains("prepa 2") || (clean.contains("bachillerato") && clean.contains("años")))
            return "bachillerato 2 años";
        if (clean.contains("carreras ejecutivas") || clean.contains("carrera ejecutiva") || clean.contains("licenciatura ejecutiva") || clean.contains("ingenieria ejecutiva"))
            return "carreras ejecutivas";
        if (clean.contains("titulacion") || clean.contains("experiencia laboral") || clean.contains("titularme"))
            return "titulacion experiencia";
        if (clean.contains("ingles") || clean.contains("curso de ingles"))
            return "ingles";
        return null;
    }

    // =========================
    // BUSCAR EN KNOWLEDGE POR INTENCIÓN + ENTIDAD
    // =========================
    private KnowledgeItem buscarPorIntencionYEntidad(String intencion, String entidad) {
        if (intencion == null) return null;
        for (KnowledgeItem item : knowledge) {
            if (item.getIntencion() != null && item.getIntencion().equals(intencion)) {
                // Si el item requiere entidad, comprobamos
                if (item.getEntidades() != null && !item.getEntidades().isEmpty()) {
                    if (entidad != null && item.getEntidades().contains(entidad)) {
                        return item;
                    }
                } else {
                    // Ítem sin entidad específica (ej. horario)
                    return item;
                }
            }
        }
        return null;
    }

    // =========================
    // BÚSQUEDA POR KEYWORDS (con puntuación)
    // =========================
    private KnowledgeItem buscarPorKeywords(String mensaje) {
        String[] palabras = mensaje.split("\\s+");
        List<String> palabrasStem = new ArrayList<>();
        for (String p : palabras) {
            palabrasStem.add(stem(normalize(p)));
        }

        int maxScore = 0;
        KnowledgeItem mejorItem = null;

        for (KnowledgeItem item : knowledge) {
            int score = 0;
            for (String keyword : item.getKeywords()) {
                String keywordStem = stem(normalize(keyword));
                for (String palabraStem : palabrasStem) {
                    int dist = levenshtein(palabraStem, keywordStem);
                    if (dist <= 2) {
                        score += (3 - dist); // a menor distancia, mayor puntaje
                    } else if (keywordStem.contains(palabraStem) || palabraStem.contains(keywordStem)) {
                        score += 1;
                    }
                }
            }
            // Bonus si coincide entidad
            String entidadDetectada = detectarEntidad(mensaje);
            if (entidadDetectada != null && item.getEntidades() != null && item.getEntidades().contains(entidadDetectada)) {
                score += 5;
            }
            if (score > maxScore) {
                maxScore = score;
                mejorItem = item;
            }
        }

        return (maxScore >= 3) ? mejorItem : null; // umbral mínimo
    }

    // =========================
    // RESPUESTA PRINCIPAL (con contexto)
    // =========================
    public String getResponse(String message, Map<String, Object> session) {
        if (message == null || message.isBlank()) {
            return "¿En qué puedo ayudarte?";
        }

        String cleanMessage = normalize(message);
        cleanMessage = cleanMessage.replace("?", "").replace("¿", "").replace(".", "");

        // ----------------------------------------------------------------
        // NUEVO: Detección de ambigüedad para bachillerato
        // ----------------------------------------------------------------
        if ((cleanMessage.contains("bachillerato") || cleanMessage.contains("prepa") || cleanMessage.contains("preparatoria"))
                && !cleanMessage.contains("286") && !cleanMessage.contains("2")
                && !cleanMessage.contains("dos") && !cleanMessage.contains("años")) {
            return "Contamos con dos opciones de bachillerato:\n\n" +
                    "🎓 **Bachillerato por Acuerdo 286** (para mayores de 18 años, duración 8 semanas)\n" +
                    "🎓 **Bachillerato en 2 años** (con especialidades tecnológicas, para menores de 18 años)\n\n" +
                    "¿Sobre cuál te gustaría recibir más información?";
        }

        // 1. Detectar intención y entidad
        String intencion = detectarIntencion(cleanMessage);
        String entidad = detectarEntidad(cleanMessage);

        // 2. Intentar buscar por intención + entidad (más preciso)
        KnowledgeItem respuestaItem = null;
        if (intencion != null) {
            respuestaItem = buscarPorIntencionYEntidad(intencion, entidad);
        }

        // 3. Si no se encontró, buscar por keywords
        if (respuestaItem == null) {
            respuestaItem = buscarPorKeywords(cleanMessage);
        }

        // 4. Si hay un tema reciente y la intención es genérica, usar contexto
        if (respuestaItem == null && intencion != null && session.containsKey("ultimoTema")) {
            String ultimoTema = (String) session.get("ultimoTema");
            // Buscar un item que tenga la misma intención y cuya entidad esté en el último tema
            for (KnowledgeItem item : knowledge) {
                if (item.getIntencion() != null && item.getIntencion().equals(intencion) &&
                        item.getEntidades() != null && item.getEntidades().stream().anyMatch(e -> ultimoTema.contains(e))) {
                    respuestaItem = item;
                    break;
                }
            }
        }

        // 5. Guardar contexto si encontramos algo
        if (respuestaItem != null) {
            session.put("ultimoTema", respuestaItem.getTitulo());
            session.put("ultimaIntencion", respuestaItem.getIntencion());
            session.put("ultimaEntidad", entidad);
            // Seguridad extra: si la respuesta es null por algún motivo, devolver mensaje genérico
            String resp = respuestaItem.getRespuesta();
            return resp != null ? resp : "Información no disponible para este tema.";
        }

        // 6. Fallback
        return "No encontré información específica. Puedes preguntarme sobre bachillerato, carreras, requisitos, horarios, etc.";
    }
}