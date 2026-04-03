package com.flingerbit;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class LanguageSpec {
    public final String extension;
    public final String name;
    public final List<String> keywords;
    public final List<SuggestionEngine.Snippet> snippets;

    public LanguageSpec(String extension, String name, List<String> keywords, List<SuggestionEngine.Snippet> snippets) {
        this.extension = extension;
        this.name = name;
        this.keywords = keywords;
        this.snippets = snippets;
    }

    public static LanguageSpec fromFileName(String fileName) {
        String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".html") || lower.endsWith(".htm")) {
            return new LanguageSpec(
                    "html",
                    "HTML",
                    Arrays.asList("html", "head", "body", "div", "script", "style", "title", "link", "meta"),
                    Arrays.asList(
                            new SuggestionEngine.Snippet("<!DOCTYPE html>", "doctype"),
                            new SuggestionEngine.Snippet("<html lang=\"es\">", "html"),
                            new SuggestionEngine.Snippet("<head>\n    <meta charset=\"UTF-8\">\n</head>", "head"),
                            new SuggestionEngine.Snippet("<body>\n</body>", "body"),
                            new SuggestionEngine.Snippet("<script>\n</script>", "script"),
                            new SuggestionEngine.Snippet("<style>\n</style>", "style")
                    )
            );
        }

        if (lower.endsWith(".css")) {
            return new LanguageSpec(
                    "css",
                    "CSS",
                    Arrays.asList("body", "display", "flex", "grid", "padding", "margin", "color", "background", "font-size"),
                    Arrays.asList(
                            new SuggestionEngine.Snippet("body {\n    margin: 0;\n    padding: 0;\n}", "body"),
                            new SuggestionEngine.Snippet("display: flex;", "flex"),
                            new SuggestionEngine.Snippet("display: grid;", "grid")
                    )
            );
        }

        if (lower.endsWith(".js")) {
            return new LanguageSpec(
                    "javascript",
                    "JavaScript",
                    Arrays.asList("function", "const", "let", "var", "return", "if", "else", "class", "console", "document"),
                    Arrays.asList(
                            new SuggestionEngine.Snippet("console.log()", "log"),
                            new SuggestionEngine.Snippet("function name() {\n}", "function"),
                            new SuggestionEngine.Snippet("const name = () => {\n};", "arrow function"),
                            new SuggestionEngine.Snippet("document.getElementById()", "dom")
                    )
            );
        }

        if (lower.endsWith(".java")) {
            return new LanguageSpec(
                    "java",
                    "Java",
                    Arrays.asList("public", "class", "static", "void", "new", "return", "if", "else", "package", "import"),
                    Arrays.asList(
                            new SuggestionEngine.Snippet("public class Main {\n}", "class"),
                            new SuggestionEngine.Snippet("public static void main(String[] args) {\n}", "main"),
                            new SuggestionEngine.Snippet("System.out.println();", "print"),
                            new SuggestionEngine.Snippet("import android.os.Bundle;", "bundle")
                    )
            );
        }

        if (lower.endsWith(".kt")) {
            return new LanguageSpec(
                    "kotlin",
                    "Kotlin",
                    Arrays.asList("fun", "val", "var", "class", "object", "when", "if", "else", "import", "package"),
                    Arrays.asList(
                            new SuggestionEngine.Snippet("fun main() {\n}", "main"),
                            new SuggestionEngine.Snippet("println()", "print"),
                            new SuggestionEngine.Snippet("class Name {\n}", "class"),
                            new SuggestionEngine.Snippet("object App {\n}", "object")
                    )
            );
        }

        if (lower.endsWith(".xml")) {
            return new LanguageSpec(
                    "xml",
                    "XML",
                    Arrays.asList("android:", "xmlns:", "layout_width", "layout_height", "match_parent", "wrap_content"),
                    Arrays.asList(
                            new SuggestionEngine.Snippet("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "xml header"),
                            new SuggestionEngine.Snippet("<LinearLayout>\n</LinearLayout>", "layout"),
                            new SuggestionEngine.Snippet("<TextView />", "textview")
                    )
            );
        }

        if (lower.endsWith(".py")) {
            return new LanguageSpec(
                    "python",
                    "Python",
                    Arrays.asList("def", "class", "import", "from", "return", "if", "else", "elif", "print"),
                    Arrays.asList(
                            new SuggestionEngine.Snippet("def main():\n    pass", "main"),
                            new SuggestionEngine.Snippet("print()", "print"),
                            new SuggestionEngine.Snippet("class Name:\n    pass", "class")
                    )
            );
        }

        return new LanguageSpec(
                "text",
                "Text",
                Arrays.asList(),
                Arrays.asList(
                        new SuggestionEngine.Snippet("save", "save"),
                        new SuggestionEngine.Snippet("run", "run"),
                        new SuggestionEngine.Snippet("new file", "new file")
                )
        );
    }
}
