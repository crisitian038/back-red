package red.back.backred.chatbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.SpanishStemmer;

import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AdvancedChatService {

    private List<KnowledgeItem> knowledge;

    @Autowired(required = false)
    private ConversationLogRepository conversationLogRepository;

    // Diccionarios para mejor comprensión del lenguaje
    private static final Map<String, String[]> SINONIMOS = new HashMap<>();
    private static final Map<String, String[]> PALABRAS_NEGATIVAS = new HashMap<>();

    static {
        // Sinónimos para mejorar búsqueda
        SINONIMOS.put("requisitos", new String[]{"documentos", "necesito", "que necesito", "requisito", "requerimiento"});
        SINONIMOS.put("costo", new String[]{"precio", "colegiatura", "pago", "cuanto cuesta", "cuánto cuesta", "valor"});
        SINONIMOS.put("horario", new String[]{"hora", "cuando", "cuándo", "abren", "cierre", "atencion", "atención"});
        SINONIMOS.put("ubicacion", new String[]{"direccion", "dirección", "donde", "dónde", "localidad", "mapa", "sede"});
        SINONIMOS.put("informacion", new String[]{"info", "cuéntame", "cuentame", "explica", "explícame", "que es", "qué es"});

        // Palabras negativas
        PALABRAS_NEGATIVAS.put("no", new String[]{"ningun", "ningún", "nada", "nunca", "jamas", "jamás", "no quiero"});
    }

    @PostConstruct
    public void loadKnowledge() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = new ClassPathResource("knowledge.json").getInputStream();
            knowledge = mapper.readValue(input, new TypeReference<List<KnowledgeItem>>() {});
            System.out.println("[✓] Base de conocimiento cargada: " + knowledge.size() + " items");
        } catch (Exception e) {
            e.printStackTrace();
            knowledge = new ArrayList<>();
        }

        for (KnowledgeItem item : knowledge) {
            if (item.getRespuesta() == null || item.getRespuesta().isBlank()) {
                item.setRespuesta("Información no disponible temporalmente. Por favor contacta a soporte.");
            }
        }
    }

    // ============================================================================
    // NORMALIZACIÓN Y STEMMING
    // ============================================================================

    private String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").toLowerCase().trim();
    }

    private String stem(String word) {
        if (word == null || word.isEmpty()) return word;
        SpanishStemmer stemmer = new SpanishStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    // ============================================================================
    // ALGORITMOS DE SIMILITUD
    // ============================================================================

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

    // Similitud Jaccaria (basada en palabras compartidas)
    private double jaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) return 1.0;
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    // ============================================================================
    // DETECCIÓN AVANZADA DE INTENCIÓN
    // ============================================================================

    private class IntentResult {
        String intent;
        Double confidence;
        String detectedPhrase;

        IntentResult(String intent, Double confidence, String detectedPhrase) {
            this.intent = intent;
            this.confidence = confidence;
            this.detectedPhrase = detectedPhrase;
        }
    }

    private IntentResult detectarIntencionAvanzada(String mensaje) {
        String clean = normalize(mensaje);

        String[][] intencionPatrones = {
                {"requisitos", "requisitos?|documentos?|necesito|inscripcion|inscribir|enviar|presentar|tramite|papeles|que (necesito|documentos|papeles)|cuales son los requisitos|que necesito para"},
                {"costo", "costo|precio|colegiatura|pago|cuanto cuesta|cuánto cuesta|valor|tarifa|mensualidad|beca|descuento|financiamiento|formas? de pago|métodos? de pago|pagos"},
                {"horario", "horario|hora|cuando|abren|cierre|dias|atencion|turno|qué hora|qué días|a qué hora|fechas? inicio|calendario|convocatoria|fines de semana|sábado|domingo|finde|clases (sabado|domingo)|grupos (sabatinos|dominicales)"},
                {"ubicacion", "ubicacion|direccion|donde|dónde|localidad|mapa|sede|sucursal|oficina|ciudad"},
                {"contacto", "telefono|llamar|contacto|email|correo|whatsapp|escribir|número|comunicarme"},
                {"inscripcion", "inscribirme|inscripcion|inscribir|matricularme|matricular|registrarme|registrar|como inscribirme|como matricularme|como registrarme|quiero inscribirme|pasos para inscribirme|proceso inscripcion"},
                {"info_general", "que es|informacion|cuentame|explica|cuales|como|programa|carrera|estudiar|oferta|opciones|qué es|cuéntame|explícame|cómo|qué programas|qué carreras|qué ofrecen|que ofrecen"},
                {"soporte", "problema|falla|error|no funciona|no puedo entrar|contraseña|olvide|recuperar|soporte|tecnico|ayuda tecnica|plataforma|bug"},
                {"becas", "beca|descuento|apoyo economico|ayuda financiera|convenio|empresa|descuento empleados"},
                {"egresados", "egresado|exalumno|bolsa trabajo|seguimiento|titulado|ya me gradue|despues de graduarme"},
                {"equivalencias", "equivalencia|revalidacion|convalidar|homologar|estudios previos|transferencia"}
        };
        double maxScore = 0;
        String bestIntent = null;
        String detectedPhrase = "";

        for (String[] patron : intencionPatrones) {
            String intentName = patron[0];
            String regex = patron[1];

            Pattern p = Pattern.compile("\\b(" + regex + ")\\b", Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(clean);

            while (m.find()) {
                String matchedText = m.group(1);
                double score = 1.5;

                // Bonos por palabras clave adicionales
                if (clean.contains(intentName)) score += 1.0;

                // Bonos por contexto según intención
                if (intentName.equals("costo") && (clean.contains("beca") || clean.contains("descuento"))) score += 0.8;
                if (intentName.equals("inscripcion") && clean.contains("pasos")) score += 0.5;

                if (score > maxScore) {
                    maxScore = score;
                    bestIntent = intentName;
                    detectedPhrase = matchedText;
                }
            }
        }

        // Intenciones especiales sin palabras clave fijas
        if (bestIntent == null) {
            if (clean.matches(".*\\b(hola|buenos dias|buenas tardes|buenas noches|saludos|que tal|hey)\\b.*")) {
                return new IntentResult("saludo", 0.8, "saludo");
            }
            if (clean.matches(".*\\b(gracias|thanks|agradezco|muchas gracias|gracias por)\\b.*")) {
                return new IntentResult("agradecimiento", 0.9, "gracias");
            }
            if (clean.matches(".*\\b(adios|bye|nos vemos|hasta luego|hasta pronto)\\b.*")) {
                return new IntentResult("despedida", 0.8, "adios");
            }
        }

        double confidence = bestIntent != null ? Math.min(1.0, maxScore / 5.0) : 0.0;
        return new IntentResult(bestIntent, confidence, detectedPhrase);
    }
    // ============================================================================
    // DETECCIÓN AVANZADA DE ENTIDAD
    // ============================================================================

    private class EntityResult {
        String entity;
        Double confidence;

        EntityResult(String entity, Double confidence) {
            this.entity = entity;
            this.confidence = confidence;
        }
    }

    private EntityResult detectarEntidadAvanzada(String mensaje) {
        String clean = normalize(mensaje);

        // Patrones más específicos y con contexto
        String[][] entityPatterns = {
            {"bachillerato 286", "bachillerato.*286|acuerdo.*286|prepa.*286|286.*bachillerato|bachillerato.*acuerdo.*286"},
            {"bachillerato 2 años", "bachillerato.*2.*ano|bachillerato.*dos.*ano|prepa.*2.*ano|two.*year.*bachillerato|bachillerato.*regular|bachillerato.*estandar"},
            {"carreras ejecutivas", "carrera.*ejecutiva|licenciatura.*ejecutiva|ingenieria.*ejecutiva|programa.*ejecutivo|estudio.*ejecutivo"},
            {"titulacion experiencia", "titulacion.*experiencia|titulo.*experiencia|experiencia.*laboral|validez.*oficial|reconocimiento.*oficial"},
            {"ingles", "ingles|english|curso.*ingles|clases.*ingles|aprender.*ingles|idioma.*ingles"},
            {"bachillerato general", "bachillerato|preparatoria|prepa|secundaria|medio.*superior"}
        };

        double maxScore = 0;
        String bestEntity = null;
        String detectedPhrase = "";

        for (String[] pattern : entityPatterns) {
            String entity = pattern[0];
            String regex = pattern[1];

            Pattern p = Pattern.compile("(" + regex + ")");
            java.util.regex.Matcher m = p.matcher(clean);

            while (m.find()) {
                String matchedText = m.group(1);
                double score = 2.0; // Base score por coincidencia

                // Bonus por palabras específicas de entidad
                if (clean.contains(entity.replace(" ", "")) ||
                    clean.contains(entity.replace(" ", ".*"))) {
                    score += 1.5;
                }

                // Bonus por contexto específico
                if (clean.contains("286") && entity.contains("286")) {
                    score += 2.0; // Muy específico para bachillerato 286
                }
                if ((clean.contains("2") || clean.contains("dos")) && entity.contains("2 años")) {
                    score += 1.5; // Específico para bachillerato 2 años
                }
                if (clean.contains("ejecutiva") || clean.contains("ejecutivo")) {
                    score += 1.0; // Contexto ejecutivo
                }

                // Penalización por ambigüedad
                if (clean.contains("bachillerato") && !clean.contains("286") && !clean.contains("2")) {
                    score -= 0.5; // Podría ser cualquiera de los dos
                }

                if (score > maxScore) {
                    maxScore = score;
                    bestEntity = entity; // Tomar la entidad
                    detectedPhrase = matchedText;
                }
            }
        }

        // Si no hay entidad clara pero menciona programas, sugerir ambigüedad
        if (bestEntity == null && (clean.contains("bachillerato") || clean.contains("carrera") || clean.contains("programa"))) {
            if (clean.contains("bachillerato") && !clean.contains("286") && !clean.contains("2")) {
                return new EntityResult("bachillerato_ambiguo", 0.6);
            }
        }

        double confidence = bestEntity != null ? Math.min(1.0, maxScore / 6.0) : 0.0;
        return new EntityResult(bestEntity, confidence);
    }

    // ============================================================================
    // BÚSQUEDA INTELIGENTE EN KNOWLEDGE BASE
    // ============================================================================

    private class SearchResult {
        KnowledgeItem item;
        Double score;

        SearchResult(KnowledgeItem item, Double score) {
            this.item = item;
            this.score = score;
        }
    }

    private SearchResult buscarInteligente(String mensaje, String intencion, String entidad) {
        String clean = normalize(mensaje);
        String[] palabras = clean.split("\\s+");

        List<String> palabrasStem = Arrays.stream(palabras)
                .map(this::stem)
                .filter(w -> w.length() > 2)
                .collect(Collectors.toList());

        double mejorScore = 0;
        KnowledgeItem mejorItem = null;

        for (KnowledgeItem item : knowledge) {
            double score = 0;

            // 1. Puntuación por intención exacta
            if (intencion != null && item.getIntencion() != null &&
                item.getIntencion().equals(intencion)) {
                score += 7.0;
            }

            // 2. Puntuación por entidad exacta
            if (entidad != null && item.getEntidades() != null &&
                item.getEntidades().contains(entidad)) {
                score += 7.0;
            }

            // 3. Puntuación por keywords
            if (item.getKeywords() != null) {
                for (String keyword : item.getKeywords()) {
                    String keywordStem = stem(normalize(keyword));

                    for (String palabraStem : palabrasStem) {
                        int dist = levenshtein(palabraStem, keywordStem);

                        if (dist == 0) {
                            score += 4.0; // Coincidencia exacta
                        } else if (dist <= 1) {
                            score += 2.5; // Coincidencia muy cercana
                        } else if (dist <= 2) {
                            score += 1.0; // Coincidencia cercana
                        } else if (keywordStem.contains(palabraStem) || palabraStem.contains(keywordStem)) {
                            score += 0.5; // Coincidencia parcial
                        }
                    }
                }
            }

            if (score > mejorScore) {
                mejorScore = score;
                mejorItem = item;
            }
        }

        // Umbral mínimo de confianza
        return (mejorScore >= 2.5) ? new SearchResult(mejorItem, mejorScore) :
               new SearchResult(null, mejorScore);
    }

    // ============================================================================
    // SUGERENCIAS INTELIGENTES
    // ============================================================================

    private List<String> generarSugerencias(String intencion, String entidad) {
        List<String> sugerencias = new ArrayList<>();

        if (intencion != null) {
            switch (intencion) {
                case "requisitos":
                    if (entidad != null && !entidad.isEmpty()) {
                        sugerencias.add("¿Cuáles son los requisitos para " + entidad + "?");
                        sugerencias.add("¿Qué documentos necesito para " + entidad + "?");
                    } else {
                        sugerencias.add("¿Qué documentos necesito en general?");
                        sugerencias.add("¿Hay requisitos especiales para mayores de edad?");
                    }
                    break;
                case "costo":
                    if (entidad != null && !entidad.isEmpty()) {
                        sugerencias.add("¿Cuánto cuesta " + entidad + "?");
                        sugerencias.add("¿Hay becas para " + entidad + "?");
                    } else {
                        sugerencias.add("¿Cuánto cuestan los programas?");
                        sugerencias.add("¿Hay descuentos por pago anticipado?");
                    }
                    break;
                case "horario":
                    sugerencias.add("¿Cuál es el horario de clases?");
                    sugerencias.add("¿Hay grupos los fines de semana?");
                    break;
                case "ubicacion":
                    sugerencias.add("¿Dónde están ubicados?");
                    sugerencias.add("¿Tienen estacionamiento?");
                    break;
                case "contacto":
                    sugerencias.add("¿Tienen WhatsApp?");
                    sugerencias.add("¿Cuál es el correo de contacto?");
                    break;
                case "inscripcion":
                    sugerencias.add("¿Cómo me inscribo?");
                    sugerencias.add("¿Cuándo son las inscripciones?");
                    break;
                case "soporte":
                    sugerencias.add("No puedo acceder a la plataforma");
                    sugerencias.add("Olvidé mi contraseña");
                    break;
                case "becas":
                    sugerencias.add("¿Qué tipos de becas ofrecen?");
                    sugerencias.add("¿Cómo solicito una beca?");
                    break;
                case "info_general":
                    sugerencias.add("¿Qué programas ofrecen?");
                    sugerencias.add("¿Cuál es la duración de las carreras?");
                    break;
                default:
                    sugerencias.add("¿Cuáles son los programas disponibles?");
                    sugerencias.add("¿Cómo puedo inscribirme?");
                    break;
            }
        } else if (entidad != null && !entidad.isEmpty()) {
            if (entidad.contains("bachillerato")) {
                sugerencias.add("¿Cuáles son los requisitos para bachillerato?");
                sugerencias.add("¿Cuánto dura el bachillerato?");
            } else if (entidad.contains("carreras")) {
                sugerencias.add("¿Qué carreras ejecutivas ofrecen?");
                sugerencias.add("¿Cuál es la modalidad de las carreras?");
            } else if (entidad.contains("ingles")) {
                sugerencias.add("¿Cuánto dura el curso de inglés?");
                sugerencias.add("¿Cuál es el horario del curso de inglés?");
            }
        } else {
            sugerencias.add("¿Qué programas ofrecen?");
            sugerencias.add("¿Dónde están ubicados?");
            sugerencias.add("¿Cuál es el horario de atención?");
        }

        return sugerencias.stream().limit(3).collect(Collectors.toList());
    }
    // ============================================================================
    // MÉTODO PRINCIPAL: OBTENER RESPUESTA
    // ============================================================================

    public ChatResponse obtenerRespuestaAvanzada(String message, Map<String, Object> session, String sessionId) {
        if (message == null || message.isBlank()) {
            return new ChatResponse("¿En qué puedo ayudarte?", 0.5);
        }

        String cleanMessage = normalize(message);
        cleanMessage = cleanMessage.replace("?", "").replace("¿", "").replace(".", "");

        // Detectar intención y entidad
        IntentResult intentResult = detectarIntencionAvanzada(cleanMessage);
        EntityResult entityResult = detectarEntidadAvanzada(cleanMessage);

        String intencion = intentResult.intent;
        String entidad = entityResult.entity;
        Double intencionConfianza = intentResult.confidence;
        Double entidadConfianza = entityResult.confidence;

        // Búsqueda inteligente
        SearchResult searchResult = buscarInteligente(cleanMessage, intencion, entidad);

        ChatResponse response = null;

        if (searchResult.item != null) {
            double confidenceScore = Math.min(1.0, (intencionConfianza + entidadConfianza + searchResult.score) / 5.0);
            response = new ChatResponse(searchResult.item.getRespuesta(), confidenceScore);
            response.setDetectedIntent(intencion);
            response.setDetectedEntity(entidad);
            response.setSuggestedQuestions(generarSugerencias(intencion, entidad));

            // Guardar en sesión
            session.put("ultimoTema", searchResult.item.getTitulo());
            session.put("ultimaIntencion", intencion);
            session.put("ultimaEntidad", entidad);
        } else {
            // Manejar ambigüedades específicas
            if (entidad != null && entidad.equals("bachillerato_ambiguo")) {
                String ambiguedadMsg = "¡Excelente pregunta! Ofrecemos dos tipos de bachillerato:\n\n" +
                        "🎓 **Bachillerato 286** (3 años):\n" +
                        "• Acuerdo 286 con validez oficial\n" +
                        "• Ideal para estudiantes con trayectoria académica regular\n" +
                        "• Mayor tiempo para desarrollo integral\n\n" +
                        "⚡ **Bachillerato 2 años** (acelerado):\n" +
                        "• Programa intensivo\n" +
                        "• Para estudiantes con conocimientos previos sólidos\n" +
                        "• Concluye en menos tiempo\n\n" +
                        "¿Cuál de los dos te interesa más? Puedo darte información específica sobre requisitos, costos o cualquier detalle.";
                response = new ChatResponse(ambiguedadMsg, 0.8);
                response.setDetectedIntent(intencion);
                response.setDetectedEntity("bachillerato_ambiguo");
                response.setSuggestedQuestions(Arrays.asList(
                    "¿Cuáles son los requisitos para el Bachillerato 286?",
                    "¿Cuál es el costo del Bachillerato 2 años?",
                    "¿En qué se diferencia el Bachillerato 286 del regular?"
                ));
            } else {
                // Fallback general con sugerencias contextuales
                String fallbackMsg = "No estoy completamente seguro de tu pregunta. 🤔\n\n";

                if (intencion != null) {
                    fallbackMsg += "Parece que preguntas sobre " + intencion + ". ";
                }

                fallbackMsg += "Puedo ayudarte con:\n" +
                        "📚 Información sobre nuestros programas educativos\n" +
                        "💰 Costos, mensualidades y formas de pago\n" +
                        "📅 Horarios de clases y atención\n" +
                        "📍 Ubicaciones de nuestras sedes\n" +
                        "📞 Información de contacto\n" +
                        "📋 Requisitos de inscripción\n\n" +
                        "¿Podrías ser más específico?";

                response = new ChatResponse(fallbackMsg, 0.3);
                response.setDetectedIntent(intencion);
                response.setSuggestedQuestions(Arrays.asList(
                    "¿Cuáles programas ofrecen?",
                    "¿Cómo puedo inscribirme?",
                    "¿Cuál es el costo de los programas?"
                ));
            }
        }

        // Registrar conversación si hay repositorio
        if (conversationLogRepository != null) {
            ConversationLog log = new ConversationLog();
            log.setUserMessage(message);
            log.setBotResponse(response.getResponse());
            log.setDetectedIntent(intencion);
            log.setDetectedEntity(entidad);
            log.setConfidenceScore(response.getConfidenceScore());
            log.setSessionId(sessionId);
            log.setCreatedAt(LocalDateTime.now());
            try {
                conversationLogRepository.save(log);
            } catch (Exception e) {
                System.out.println("[!] Error al guardar log de conversación: " + e.getMessage());
            }
        }

        return response;
    }
}
