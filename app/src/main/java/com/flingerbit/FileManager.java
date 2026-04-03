package com.flingerbit;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class FileManager {

    private final Context context;

    public FileManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public File getProjectDir() {
        File base = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (base == null) {
            base = context.getFilesDir();
        }

        File dir = new File(base, "FlingerBit");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public File resolve(String fileName) {
        return new File(getProjectDir(), fileName);
    }

    public void save(String fileName, String content) throws IOException {
        File file = resolve(fileName);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String read(String fileName) throws IOException {
        File file = resolve(fileName);
        if (!file.exists()) return "";
        return new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    public String templateFor(String fileName) {
        String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".html") || lower.endsWith(".htm")) {
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Flinger-Bit</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1>Flinger-Bit</h1>\n" +
                    "    <p>Escribe tu HTML aquí.</p>\n" +
                    "    <a href=\"data:text/plain;charset=utf-8,Hola%20desde%20Flinger-Bit\" download=\"hola.txt\">Descargar archivo</a>\n" +
                    "</body>\n" +
                    "</html>\n";
        }

        if (lower.endsWith(".css")) {
            return "body {\n    margin: 0;\n    font-family: sans-serif;\n    background: #0b1020;\n    color: white;\n}\n";
        }

        if (lower.endsWith(".js")) {
            return "console.log('Flinger-Bit');\n";
        }

        if (lower.endsWith(".java")) {
            return "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Flinger-Bit\");\n    }\n}\n";
        }

        if (lower.endsWith(".kt")) {
            return "fun main() {\n    println(\"Flinger-Bit\")\n}\n";
        }

        if (lower.endsWith(".xml")) {
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"match_parent\"\n    android:orientation=\"vertical\" />\n";
        }

        if (lower.endsWith(".py")) {
            return "def main():\n    print('Flinger-Bit')\n\nif __name__ == '__main__':\n    main()\n";
        }

        return "";
    }

    public String buildPreviewHtml(String fileName, String code) {
        String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".html") || lower.endsWith(".htm")) {
            return code;
        }

        if (lower.endsWith(".css")) {
            String safeCss = escapeForStyle(code);
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <style>\n" + safeCss + "\n    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"card\">\n" +
                    "        <h1>CSS Preview</h1>\n" +
                    "        <p>Vista previa generada dentro de la app.</p>\n" +
                    "        <button>Botón</button>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
        }

        if (lower.endsWith(".js")) {
            String safeJs = escapeForScript(code);
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"es\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>JavaScript Preview</title>\n" +
                    "    <style>\n" +
                    "        body { font-family: sans-serif; background: #0b1020; color: #eef3ff; padding: 16px; }\n" +
                    "        pre { white-space: pre-wrap; background: #111a33; padding: 12px; border-radius: 12px; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1>JavaScript Preview</h1>\n" +
                    "    <pre id=\"out\">Ejecutando...</pre>\n" +
                    "    <script>\n" +
                    "        const out = document.getElementById('out');\n" +
                    "        console.log = (...args) => { out.textContent += '\\n' + args.join(' '); };\n" +
                    "        window.onerror = function(message, source, line, col) {\n" +
                    "            out.textContent = 'ERROR: ' + message + ' (' + line + ':' + col + ')';\n" +
                    "            return true;\n" +
                    "        };\n" +
                    "        try {\n" +
                    safeJs + "\n" +
                    "        } catch (e) {\n" +
                    "            out.textContent = 'ERROR: ' + e.message;\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";
        }

        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Preview</title>\n" +
                "    <style>\n" +
                "        body { font-family: sans-serif; background: #0b1020; color: #eef3ff; padding: 16px; }\n" +
                "        pre { white-space: pre-wrap; background: #111a33; padding: 12px; border-radius: 12px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Preview</h1>\n" +
                "    <pre>" + escapeHtml(code) + "</pre>\n" +
                "</body>\n" +
                "</html>";
    }

    private static String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String escapeForScript(String value) {
        if (value == null) return "";
        return value.replace("</script>", "<\\/script>");
    }

    private static String escapeForStyle(String value) {
        if (value == null) return "";
        return value.replace("</style>", "<\\/style>");
    }
            }
