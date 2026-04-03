package com.flingerbit;

public final class CodeHighlighter {
    private CodeHighlighter() {}

    public static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(value.length() + 32);
        for (char c : value.toCharArray()) {
            switch (c) {
                case '&': out.append("&amp;"); break;
                case '<': out.append("&lt;"); break;
                case '>': out.append("&gt;"); break;
                case '"': out.append("&quot;"); break;
                case '\'': out.append("&#39;"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }

    public static String wrapDocument(String title, String bodyHtml) {
        String safeTitle = escapeHtml(title);
        return "<!doctype html>"
                + "<html><head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>" + safeTitle + "</title>"
                + "<style>"
                + "body{margin:0;padding:16px;background:#0b1020;color:#eef3ff;font-family:sans-serif;}"
                + "h1,h2,p{margin:0 0 12px 0;}"
                + "pre{white-space:pre-wrap;word-wrap:break-word;background:#111a33;padding:16px;border-radius:12px;}"
                + "code{font-family:monospace;}"
                + "</style>"
                + "</head><body>"
                + bodyHtml
                + "</body></html>";
    }
}
