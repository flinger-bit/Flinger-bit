cmake_minimum_required(VERSION 3.10.2)

# Proyecto de biblioteca nativa (shared)
project("native_lib")

# Agrega tu código nativo aquí
# (si no tienes archivos .cpp aún, puedes mantenerlo vacío)
add_library(
    # Nombre interno de la librería
    native-lib

    # Tipo de librería
    SHARED

    # Ruta a tus fuentes C++ (si existen)
    src/main/cpp/native-lib.cpp
)

# Encuentra la librería de log de Android
find_library(
    log-lib
    log
)

# Vincula la librería nativa contra log-lib
target_link_libraries(
    native-lib
    ${log-lib}
)
