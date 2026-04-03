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

    public List<Suggestion> suggest(LanguageSpec spec, String text, int cursor) {
        String before = safeSubstring(text, 0, cursor);
        String lastToken = extractLastToken(before).toLowerCase(Locale.ROOT);

        List<Suggestion> out = new ArrayList<>();

        if (spec != null && "html".equals(spec.extension)) {
            if ("<doct".startsWith(lastToken) || lastToken.contains("<doct")) {
                out.add(new Suggestion("DOCTYPE", "HTML5", "<!DOCTYPE html>"));
            }
            if ("<html".startsWith(lastToken) || lastToken.contains("<html")) {
                out.add(new Suggestion("html", "Tag raíz", "<html lang=\"es\">"));
            }
            if ("<head".startsWith(lastToken)) {
                out.add(new Suggestion("head", "Cabecera", "<head>\n    <meta charset=\"UTF-8\">\n</head>"));
            }
            if ("<body".startsWith(lastToken)) {
                out.add(new Suggestion("body", "Cuerpo", "<body>\n</body>"));
            }
        }

        if (spec != null) {
            for (Snippet s : spec.snippets) {
                String label = s.label.toLowerCase(Locale.ROOT);
                String insert = s.text.toLowerCase(Locale.ROOT);
                if (lastToken.isEmpty() || label.contains(lastToken) || insert.startsWith(lastToken)) {
                    out.add(new Suggestion(s.label, "Snippet", s.text));
                }
            }
        }

        if (out.isEmpty()) {
            out.add(new Suggestion("Sin sugerencias", "Escribe más caracteres", ""));
        }

        return out;
    }

    private static String extractLastToken(String text) {
        if (text == null || text.isEmpty()) return "";
        int end = text.length() - 1;
        while (end >= 0 && Character.isWhitespace(text.charAt(end))) {
            end--;
        }
        if (end < 0) return "";
        int start = end;
        while (start >= 0) {
            char c = text.charAt(start);
            if (Character.isLetterOrDigit(c) || c == '<' || c == '!' || c == '_' || c == '-' || c == ':' || c == '/') {
                start--;
            } else {
                break;
            }
        }
        return text.substring(start + 1, end + 1);
    }

    private static String safeSubstring(String s, int start, int end) {
        if (s == null) return "";
        int e = Math.min(end, s.length());
        int st = Math.max(0, start);
        if (st >= e) return "";
        return s.substring(st, e);
    }
            }
