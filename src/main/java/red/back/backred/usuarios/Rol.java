package red.back.backred.usuarios;

public enum Rol {
    ADMIN("ADMIN"),
    USER("USER");

    private final String valor;

    Rol(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static Rol fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return USER;
        }
        try {
            return Rol.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER; // default
        }
    }
}
