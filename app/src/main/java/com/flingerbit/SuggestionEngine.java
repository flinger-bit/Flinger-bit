package com.flingerbit;

import java.util.ArrayList;
import java.util.List;

public final class SuggestionEngine {

    public static final class Suggestion {
        public final String title;
        public final String subtitle;
        public final String insertText;

        public Suggestion(String title, String subtitle, String insertText) {
            this.title = title;
            this.subtitle = subtitle;
            this.insertText = insertText;
        }
    }

    public List<Suggestion> suggest(String fileName, String text, int cursor) {
        LanguageSpec spec = LanguageSpec.fromFileName(fileName);
        List<Suggestion> out = new ArrayList<>();

        switch (spec) {
            case HTML:
            case HTM:
                out.add(new Suggestion("doctype", "Plantilla HTML básica", "<!doctype html>"));
                out.add(new Suggestion("meta charset", "Codificación UTF-8", "<meta charset=\"UTF-8\">"));
                out.add(new Suggestion("viewport", "Vista adaptable", "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"));
                out.add(new Suggestion("link css", "Hoja de estilo", "<link rel=\"stylesheet\" href=\"styles.css\">"));
                out.add(new Suggestion("script", "Script al final", "<script src=\"app.js\"></script>"));
                out.add(new Suggestion("div", "Contenedor", "<div class=\"container\"></div>"));
                break;

            case CSS:
                out.add(new Suggestion("flex", "Contenedor flexible", "display: flex;\n"));
                out.add(new Suggestion("center", "Centrar contenido", "justify-content: center;\nalign-items: center;\n"));
                out.add(new Suggestion("spacing", "Espaciado común", "padding: 16px;\ngap: 16px;\n"));
                out.add(new Suggestion("card", "Tarjeta básica", "background: #111a33;\nborder-radius: 16px;\n"));
                out.add(new Suggestion("shadow", "Sombra suave", "box-shadow: 0 10px 30px rgba(0,0,0,.25);\n"));
                break;

            case JAVASCRIPT:
                out.add(new Suggestion("log", "Salida a consola", "console.log('Flinger-Bit');\n"));
                out.add(new Suggestion("ready", "DOMContentLoaded", "document.addEventListener('DOMContentLoaded', () => {\n\n});\n"));
                out.add(new Suggestion("fetch", "Petición fetch", "fetch('https://example.com')\n  .then(r => r.text())\n  .then(console.log);\n"));
                out.add(new Suggestion("func", "Función flecha", "const main = () => {\n\n};\n"));
                break;

            case JAVA:
                out.add(new Suggestion("main", "Punto de entrada", "public static void main(String[] args) {\n\n}\n"));
                out.add(new Suggestion("println", "Imprimir en consola", "System.out.println();\n"));
                out.add(new Suggestion("class", "Clase pública", "public class Main {\n\n}\n"));
                break;

            case KOTLIN:
                out.add(new Suggestion("main", "Punto de entrada", "fun main() {\n\n}\n"));
                out.add(new Suggestion("val", "Variable inmutable", "val name = \"Flinger-Bit\"\n"));
                out.add(new Suggestion("when", "Expresión when", "when (value) {\n    else -> Unit\n}\n"));
                break;

            case PYTHON:
                out.add(new Suggestion("main", "Función principal", "def main():\n    pass\n"));
                out.add(new Suggestion("print", "Imprimir texto", "print('Flinger-Bit')\n"));
                out.add(new Suggestion("entry", "Punto de entrada", "if __name__ == '__main__':\n    main()\n"));
                break;

            default:
                out.add(new Suggestion("snippet", "Comentario", "# Flinger-Bit\n"));
                out.add(new Suggestion("hello", "Hola mundo", "Hello, world!\n"));
                break;
        }

        return out;
    }
}
