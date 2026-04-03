package com.flingerbit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SuggestionEngine {

    public static final class Snippet {
        public final String text;
        public final String label;

        public Snippet(String text, String label) {
            this.text = text;
            this.label = label;
        }
    }

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
        String beforeCursor = safeSubstring(text, 0, Math.max(0, cursor));
        String token = extractLastToken(beforeCursor).toLowerCase(Locale.ROOT);

        List<Suggestion> out = new ArrayList<>();

        switch (spec.extension) {
            case "html":
                addHtmlSuggestions(out, token);
                break;
            case "css":
                addCssSuggestions(out, token);
                break;
            case "javascript":
                addJsSuggestions(out, token);
                break;
            case "java":
                addJavaSuggestions(out, token);
                break;
            case "kotlin":
                addKotlinSuggestions(out, token);
                break;
            case "python":
                addPythonSuggestions(out, token);
                break;
            case "xml":
                addXmlSuggestions(out, token);
                break;
            default:
                break;
        }

        if (out.isEmpty()) {
            out.add(new Suggestion("Sin sugerencias", "Escribe más caracteres", ""));
        }

        return out;
    }

    private static void addHtmlSuggestions(List<Suggestion> out, String token) {
        if (token.isEmpty() || "<doct".startsWith(token)) {
            out.add(new Suggestion("DOCTYPE", "HTML5", "<!DOCTYPE html>"));
        }
        if (token.isEmpty() || "<html".startsWith(token) || token.contains("<html")) {
            out.add(new Suggestion("html", "Tag raíz", "<html lang=\"es\">"));
        }
        if (token.isEmpty() || "<head".startsWith(token)) {
            out.add(new Suggestion("head", "Cabecera", "<head>\n    <meta charset=\"UTF-8\">\n</head>"));
        }
        if (token.isEmpty() || "<body".startsWith(token)) {
            out.add(new Suggestion("body", "Cuerpo", "<body>\n</body>"));
        }
        if (token.isEmpty() || "<script".startsWith(token)) {
            out.add(new Suggestion("script", "Bloque JavaScript", "<script>\n</script>"));
        }
        if (token.isEmpty() || "<style".startsWith(token)) {
            out.add(new Suggestion("style", "Bloque CSS", "<style>\n</style>"));
        }
    }

    private static void addCssSuggestions(List<Suggestion> out, String token) {
        if (token.isEmpty() || "body".startsWith(token)) {
            out.add(new Suggestion("body { }", "Regla base", "body {\n    margin: 0;\n    padding: 0;\n}"));
        }
        if (token.isEmpty() || "display".startsWith(token)) {
            out.add(new Suggestion("display: flex", "Layout flex", "display: flex;"));
        }
        if (token.isEmpty() || "grid".startsWith(token)) {
            out.add(new Suggestion("display: grid", "Layout grid", "display: grid;"));
        }
    }

    private static void addJsSuggestions(List<Suggestion> out, String token) {
        if (token.isEmpty() || "console".startsWith(token) || "log".startsWith(token)) {
            out.add(new Suggestion("console.log()", "Imprimir en consola", "console.log();"));
        }
        if (token.isEmpty() || "function".startsWith(token)) {
            out.add(new Suggestion("function", "Función", "function name() {\n}\n"));
        }
        if (token.isEmpty() || "const".startsWith(token)) {
            out.add(new Suggestion("const", "Constante", "const name = () => {\n};"));
        }
        if (token.isEmpty() || "document".startsWith(token)) {
            out.add(new Suggestion("document.getElementById()", "DOM", "document.getElementById();"));
        }
    }

    private static void addJavaSuggestions(List<Suggestion> out, String token) {
        if (token.isEmpty() || "public".startsWith(token) || "class".startsWith(token)) {
            out.add(new Suggestion("public class", "Clase base", "public class Main {\n}\n"));
        }
        if (token.isEmpty() || "main".startsWith(token)) {
            out.add(new Suggestion("main()", "Punto de entrada", "public static void main(String[] args) {\n}\n"));
        }
        if (token.isEmpty() || "println".startsWith(token)) {
            out.add(new Suggestion("System.out.println()", "Imprimir", "System.out.println();"));
        }
    }

    private static void addKotlinSuggestions(List<Suggestion> out, String token) {
        if (token.isEmpty() || "fun".startsWith(token)) {
            out.add(new Suggestion("fun main()", "Punto de entrada", "fun main() {\n}\n"));
        }
        if (token.isEmpty() || "println".startsWith(token)) {
            out.add(new Suggestion("println()", "Imprimir", "println()"));
        }
        if (token.isEmpty() || "class".startsWith(token)) {
            out.add(new Suggestion("class", "Clase", "class Name {\n}\n"));
        }
    }

    private static void addPythonSuggestions(List<Suggestion> out, String token) {
        if (token.isEmpty() || "def".startsWith(token)) {
            out.add(new Suggestion("def main()", "Función", "def main():\n    pass\n"));
        }
        if (token.isEmpty() || "print".startsWith(token)) {
            out.add(new Suggestion("print()", "Imprimir", "print()"));
        }
        if (token.isEmpty() || "class".startsWith(token)) {
            out.add(new Suggestion("class", "Clase", "class Name:\n    pass\n"));
        }
    }

    private static void addXmlSuggestions(List<Suggestion> out, String token) {
        if (token.isEmpty() || "<?xml".startsWith(token)) {
            out.add(new Suggestion("XML header", "Cabecera XML", "<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
        }
        if (token.isEmpty() || "linearlayout".startsWith(token)) {
            out.add(new Suggestion(
                    "LinearLayout",
                    "Layout base",
                    "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                            "    android:layout_width=\"match_parent\"\n" +
                            "    android:layout_height=\"match_parent\"\n" +
                            "    android:orientation=\"vertical\">\n\n</LinearLayout>"
            ));
        }
        if (token.isEmpty() || "textview".startsWith(token)) {
            out.add(new Suggestion(
                    "TextView",
                    "Texto",
                    "<TextView\n    android:layout_width=\"wrap_content\"\n    android:layout_height=\"wrap_content\" />"
            ));
        }
    }

    private static String extractLastToken(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        int end = text.length();
        while (end > 0 && Character.isWhitespace(text.charAt(end - 1))) {
            end--;
        }

        int start = end;
        while (start > 0) {
            char c = text.charAt(start - 1);
            if (Character.isLetterOrDigit(c) || c == '<' || c == '!' || c == '/' || c == '-' || c == '_' || c == ':') {
                start--;
            } else {
                break;
            }
        }

        return text.substring(start, end).trim();
    }

    private static String safeSubstring(String text, int start, int end) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int safeEnd = Math.min(end, text.length());
        int safeStart = Math.max(0, start);
        if (safeStart >= safeEnd) {
            return "";
        }
        return text.substring(safeStart, safeEnd);
    }
}
