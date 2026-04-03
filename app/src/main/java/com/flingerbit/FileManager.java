package com.flingerbit;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

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
        String safe = fileName == null ? "untitled.txt" : fileName.trim();
        if (safe.isEmpty()) {
            safe = "untitled.txt";
        }
        return new File(getProjectDir(), safe);
    }

    public void save(String fileName, String content) throws Exception {
        File file = resolve(fileName);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write((content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
        }
    }

    public String read(String fileName) throws Exception {
        File file = resolve(fileName);
        if (!file.exists()) {
            return "";
        }
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    public String templateFor(String fileName) {
        return LanguageSpec.fromFileName(fileName).defaultTemplate();
    }

    public String buildPreviewHtml(String fileName, String code) {
        LanguageSpec spec = LanguageSpec.fromFileName(fileName);
        String safeCode = CodeHighlighter.escapeHtml(code);

        if (spec == LanguageSpec.HTML || spec == LanguageSpec.HTM) {
            return code == null ? "" : code;
        }

        return CodeHighlighter.wrapDocument(
                spec.displayName() + " Preview",
                "<h1>" + CodeHighlighter.escapeHtml(spec.displayName()) + "</h1>"
                        + "<p>Vista previa del contenido:</p>"
                        + "<pre><code>" + safeCode + "</code></pre>"
        );
    }
}
