package com.flingerbit;

import java.util.Locale;

public enum LanguageSpec {
    HTML("HTML", ".html", "<!doctype html>\n<html>\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <title>Flinger-Bit</title>\n</head>\n<body>\n  <h1>Hola desde Flinger-Bit</h1>\n</body>\n</html>\n"),
    HTM("HTML", ".htm", "<!doctype html>\n<html>\n<head>\n  <meta charset=\"UTF-8\">\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <title>Flinger-Bit</title>\n</head>\n<body>\n  <h1>Hola desde Flinger-Bit</h1>\n</body>\n</html>\n"),
    CSS("CSS", ".css", "body {\n  margin: 0;\n  font-family: sans-serif;\n  background: #0b1020;\n  color: #eef3ff;\n}\n"),
    JAVASCRIPT("JavaScript", ".js", "console.log('Flinger-Bit');\n"),
    JAVA("Java", ".java", "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Flinger-Bit\");\n    }\n}\n"),
    KOTLIN("Kotlin", ".kt", "fun main() {\n    println(\"Flinger-Bit\")\n}\n"),
    PYTHON("Python", ".py", "def main():\n    print('Flinger-Bit')\n\nif __name__ == '__main__':\n    main()\n"),
    XML("XML", ".xml", "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n</resources>\n"),
    TEXT("Texto", "", "");

    private final String displayName;
    private final String extension;
    private final String template;

    LanguageSpec(String displayName, String extension, String template) {
        this.displayName = displayName;
        this.extension = extension;
        this.template = template;
    }

    public String displayName() {
        return displayName;
    }

    public String extension() {
        return extension;
    }

    public String defaultTemplate() {
        return template;
    }

    public static LanguageSpec fromFileName(String fileName) {
        String value = fileName == null ? "" : fileName.trim().toLowerCase(Locale.ROOT);
        if (value.endsWith(".html")) return HTML;
        if (value.endsWith(".htm")) return HTM;
        if (value.endsWith(".css")) return CSS;
        if (value.endsWith(".js")) return JAVASCRIPT;
        if (value.endsWith(".java")) return JAVA;
        if (value.endsWith(".kt")) return KOTLIN;
        if (value.endsWith(".py")) return PYTHON;
        if (value.endsWith(".xml")) return XML;
        return TEXT;
    }
}
