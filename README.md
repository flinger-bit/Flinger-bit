# 🧠 Flinger‑Bit — Android Code Editor IDE

**Flinger‑Bit** es un **editor de código nativo para Android** diseñado para escribir, editar y gestionar múltiples tipos de archivos de programación directamente en tu dispositivo.  
Está pensado como una herramienta ligera y funcional para programadores que quieran codificar, guardar y ejecutar proyectos simples desde su tablet o teléfono Android.

---

## 📌 Descripción

Flinger‑Bit permite:

- 🧾 Crear y editar archivos de varios lenguajes (HTML, Java, Kotlin, JavaScript, etc.)
- 📂 Gestionar archivos y proyectos localmente
- 💡 Sugerencias de código útiles mientras escribes
- 🖥 Terminal integrada que muestra resultados y errores
- 📦 Estructura de proyecto lista para compilar con **GitHub Actions**  
  *(debug y release)*

Este repositorio contiene todo lo necesario para compilar la aplicación Android nativa con Gradle y generar un APK listo para instalar. 1

---

## 🚀 Estructura del proyecto
- 📁 **.github/workflows/** – Workflows de CI/CD para compilar con GitHub Actions  
- 📁 **app/src/main/java/com/flingerbit/** – Código fuente Java  
- 📁 **app/src/main/res/** – Recursos de UI (layouts, strings, themes)  
- 📄 **Gradle files** – Configuración de compilación  
- 📄 **gradlew**/**gradlew.bat** – Scripts del Gradle Wrapper

---

## 🧰 Tecnologías utilizadas

✔ Java  
✔ Android SDK  
✔ Android Gradle Plugin  
✔ Material Design Components  
✔ GitHub Actions para CI/CD  
*(Compilación automática de APKs)*

---

## 📦 Instalación local

1. Clona el repositorio:

```bash
git clone https://github.com/flinger-bit/Flinger-bit.git
