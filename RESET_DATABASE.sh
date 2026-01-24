#!/bin/bash
# ========================================
# INSTRUCCIONES PARA RESETEAR LA BASE DE DATOS
# ========================================

# Paso 1: Detener el servidor Spring Boot (si está corriendo)
echo "⏹️  Detén el servidor Spring Boot si está corriendo..."

# Paso 2: Ejecutar este comando en MySQL para borrar la base de datos
# Opción A: Usando mysql CLI
mysql -u root -proot -e "DROP DATABASE IF EXISTS red_educativa; CREATE DATABASE red_educativa;"

# O Opción B: Si tienes workbench u otro cliente
# 1. Abre MySQL Workbench
# 2. Ejecuta estos comandos:
#    DROP DATABASE IF EXISTS red_educativa;
#    CREATE DATABASE red_educativa;
# 3. Cierra el cliente

# Paso 3: Inicia el servidor Spring Boot nuevamente
echo "🚀 Reinicia el servidor Spring Boot..."
echo "   El servidor detectará que la base está vacía"
echo "   Hibernate creará automáticamente las tablas (ddl-auto: update)"
echo "   Spring Boot cargará automáticamente los datos de data.sql"

# Paso 4: Verifica en los logs que aparezca:
echo ""
echo "✅ Deberías ver en los logs:"
echo "   - Creación de tablas"
echo "   - Ejecución de data.sql"
echo "   - 2 usuarios insertados"
echo "   - 2 carreras insertadas"
echo "   - 2 contactos insertados"
echo "   - 2 inscripciones insertadas"

echo ""
echo "🎯 Una vez que esté todo listo:"
echo "   1. Username: admin@admin.com / Password: admin123 (rol: ADMIN)"
echo "   2. Username: usuario@prueba.com / Password: user123 (rol: USER)"
