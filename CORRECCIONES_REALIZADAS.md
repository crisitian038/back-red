# 🔧 CORRECCIONES REALIZADAS - SISTEMA DE USUARIOS

## ❌ PROBLEMA ORIGINAL
Error 500 al crear usuarios: `Data truncated for column 'rol' at row 1`

La base de datos tenía inconsistencias:
- El enum `Rol` tenía 3 valores: `ADMIN`, `USUARIO`, `USER`
- Esto causaba conflicto en la longitud de la columna

## ✅ SOLUCIONES IMPLEMENTADAS

### 1. **Rol.java** - Simplificación del Enum
**Cambio:** Reducir de 3 valores a 2 valores
```java
// ANTES
ADMIN("ADMIN"),
USUARIO("USUARIO"),
USER("USER")

// AHORA
ADMIN("ADMIN"),
USER("USER")
```

### 2. **Usuario.java** - Especificar Longitud de Columna
**Cambio:** Agregar `length = 10` al campo rol
```java
// ANTES
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Rol rol;

// AHORA
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 10)
private Rol rol;
```

### 3. **UsuarioService.java** - Mejorar Conversión de Roles
**Cambio:** Agregar lógica robusta para convertir String a Enum
```java
public Usuario crear(Usuario usuario) {
    if (usuario.getRol() == null) {
        usuario.setRol(Rol.USER);
    } else {
        try {
            String rolStr = usuario.getRol().toString();
            usuario.setRol(Rol.fromString(rolStr));
        } catch (Exception e) {
            usuario.setRol(Rol.USER); // default
        }
    }
    // ...resto del código
}
```

### 4. **DataInitializer.java** - Inicialización de Datos (NUEVO)
**Creado:** Componente que se ejecuta automáticamente después de que Hibernate crea las tablas

```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    // Inserta automáticamente 2 usuarios, 2 carreras, 2 contactos, 2 inscripciones
}
```

**Ventajas:**
- ✅ Se ejecuta DESPUÉS de que Hibernate cree las tablas
- ✅ Verifica si ya existen datos (no duplica)
- ✅ Las contraseñas se hashean automáticamente
- ✅ No necesita SQL manual

### 5. **application.yml** - Configuración Actualizada
**Cambio:** Usar `CommandLineRunner` en lugar de `data.sql`
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Crea las tablas automáticamente
  sql:
    init:
      mode: never            # No ejecuta data.sql
```

## 🚀 CÓMO APLICAR LOS CAMBIOS

### Paso 1: Limpiar la Base de Datos
```sql
DROP DATABASE IF EXISTS red_educativa;
CREATE DATABASE red_educativa;
```

### Paso 2: Reiniciar el Servidor Spring Boot
```bash
mvn spring-boot:run
```

O ejecuta desde tu IDE.

### Paso 3: Verifica en los Logs
Deberías ver:
```
🔄 Iniciando carga de datos de prueba...
📝 Insertando usuarios...
✅ 2 usuarios insertados
📝 Insertando carreras...
✅ 2 carreras insertadas
📝 Insertando contactos...
✅ 2 contactos insertados
📝 Insertando inscripciones...
✅ 2 inscripciones insertadas
✨ Carga de datos completada exitosamente
```

## 📊 DATOS DE PRUEBA INSERTADOS

### Usuarios
| Email | Password | Rol | Estado |
|-------|----------|-----|--------|
| admin@admin.com | admin123 | ADMIN | ✅ Activo |
| usuario@prueba.com | user123 | USER | ✅ Activo |

### Carreras
1. **Ingeniería en Sistemas Computacionales** - Presencial (45 solicitudes)
2. **Contabilidad** - En línea (28 solicitudes)

### Contactos
1. Juan Pérez García - Estado: PENDIENTE
2. María López Martínez - Estado: REVISADA

### Inscripciones
1. Carlos Rodríguez Sánchez - Estado: EN_PROCESO
2. Ana Martínez Torres - Estado: INSCRITO

## ⚙️ CAMBIOS EN ARCHIVOS

### Modificados
- ✅ `Rol.java` - Simplificado enum
- ✅ `Usuario.java` - Especificada longitud de columna
- ✅ `UsuarioService.java` - Mejorada conversión de roles
- ✅ `application.yml` - Configuración actualizada

### Creados
- ✅ `DataInitializer.java` - Inicializador de datos automático

### Deprecados
- ⚠️ `data.sql` - Ya no se usa (reemplazado por DataInitializer)

## 🎯 PRÓXIMOS PASOS

1. **Reinicia el servidor**
2. **Verifica que no hay error 500**
3. **Prueba crear un usuario** desde el panel de admin
4. **Confirma que aparece en la tabla**
5. **Intenta exportar datos** a Excel/PDF

## 📝 NOTAS IMPORTANTES

- Las contraseñas se hashean automáticamente con BCrypt
- El inicializador verifica si ya existen datos (no duplica en reinicies)
- Si necesitas agregar más datos de prueba, edita `DataInitializer.java`
- Para producción, cambia `ddl-auto: create-drop` a `update`

