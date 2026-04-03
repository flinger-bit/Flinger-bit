package com.flingerbit;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flingerbit.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FileManager fileManager;
    private EditorManager editorManager;
    private String currentFileName = "example.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        fileManager = new FileManager(this);
        editorManager = new EditorManager(
                this,
                getColor(R.color.accent),
                getColor(R.color.tag),
                getColor(R.color.string),
                getColor(R.color.comment)
        );

        setupWebView();

        binding.fileName.setText(currentFileName);
        editorManager.setFileName(currentFileName);

        editorManager.attach(binding.codeEditor, binding.suggestionList, insertText -> {
            int start = Math.max(0, binding.codeEditor.getSelectionStart());
            int end = Math.max(0, binding.codeEditor.getSelectionEnd());
            binding.codeEditor.getText().replace(Math.min(start, end), Math.max(start, end), insertText);
        });

        binding.codeEditor.setText(fileManager.templateFor(currentFileName));

        binding.fileName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String value = s == null ? "" : s.toString().trim();
                if (!value.isEmpty()) {
                    currentFileName = value;
                    editorManager.setFileName(currentFileName);
                }
            }
        });

        binding.btnSave.setOnClickListener(v -> saveCurrentFile());
        binding.btnRun.setOnClickListener(v -> runCurrentFile());
        binding.btnNew.setOnClickListener(v -> newFile());
        binding.btnPreview.setOnClickListener(v -> runCurrentFile());

        binding.terminal.setText("Flinger-Bit listo.");
    }

    private void setupWebView() {
        WebSettings settings = binding.previewWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        binding.previewWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                binding.terminal.setText("Preview cargada.");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                String msg = "WebView error: " + error.getDescription();
                binding.terminal.setText(msg);
            }
        });

        binding.previewWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                String msg = consoleMessage.message()
                        + " (línea " + consoleMessage.lineNumber()
                        + ", fuente " + consoleMessage.sourceId() + ")";
                binding.terminal.setText(msg);
                return true;
            }
        });

        binding.previewWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                handleDownload(url, userAgent, contentDisposition, mimeType);
            }
        });
    }

    private void newFile() {
        currentFileName = "example.html";
        binding.fileName.setText(currentFileName);
        editorManager.setFileName(currentFileName);
        editorManager.setText(fileManager.templateFor(currentFileName));
        binding.previewWebView.setVisibility(WebView.GONE);
        binding.terminal.setText("Archivo nuevo creado.");
    }

    private void saveCurrentFile() {
        try {
            currentFileName = binding.fileName.getText() == null ? "example.txt" : binding.fileName.getText().toString().trim();
            if (currentFileName.isEmpty()) currentFileName = "example.txt";

            editorManager.setFileName(currentFileName);
            fileManager.save(currentFileName, editorManager.getText());

            File saved = fileManager.resolve(currentFileName);
            binding.terminal.setText("Guardado:\n" + saved.getAbsolutePath());
            Toast.makeText(this, R.string.file_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            binding.terminal.setText("ERROR guardando:\n" + e.getMessage());
        }
    }

    private void runCurrentFile() {
        currentFileName = binding.fileName.getText() == null ? currentFileName : binding.fileName.getText().toString().trim();
        if (currentFileName.isEmpty()) currentFileName = "example.html";

        String lower = currentFileName.toLowerCase(Locale.ROOT);
        String code = editorManager.getText();

        if (lower.endsWith(".html") || lower.endsWith(".htm")
                || lower.endsWith(".css") || lower.endsWith(".js")) {
            String preview = fileManager.buildPreviewHtml(currentFileName, code);
            binding.previewWebView.setVisibility(android.view.View.VISIBLE);
            binding.previewWebView.loadDataWithBaseURL(
                    "https://flingerbit.local/",
                    preview,
                    "text/html",
                    "UTF-8",
                    null
            );
            binding.terminal.setText("Preview ejecutada para " + currentFileName);
            return;
        }

        if (lower.endsWith(".java") || lower.endsWith(".kt") || lower.endsWith(".py") || lower.endsWith(".xml")) {
            binding.previewWebView.setVisibility(android.view.View.GONE);
            binding.terminal.setText(
                    "Editor listo para guardar:\n" +
                    currentFileName + "\n\n" +
                    "La ejecución real de este lenguaje requiere un runtime/compilador dedicado dentro del APK."
            );
            return;
        }

        binding.previewWebView.setVisibility(android.view.View.GONE);
        binding.terminal.setText("No hay motor de ejecución para este archivo.");
    }

    private void handleDownload(String url, String userAgent, String contentDisposition, String mimeType) {
        try {
            if (url.startsWith("data:")) {
                saveDataUri(url, mimeType);
                return;
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.setDescription("Descargando archivo desde Flinger-Bit");
            request.addRequestHeader("User-Agent", userAgent);
            request.setMimeType(mimeType);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                binding.terminal.setText("Descarga iniciada: " + URLUtil.guessFileName(url, contentDisposition, mimeType));
            } else {
                binding.terminal.setText("DownloadManager no disponible.");
            }
        } catch (Exception e) {
            binding.terminal.setText("ERROR de descarga:\n" + e.getMessage());
        }
    }

    private void saveDataUri(String dataUri, String mimeType) {
        try {
            int comma = dataUri.indexOf(',');
            if (comma < 0) throw new IllegalArgumentException("Data URI inválida");

            String meta = dataUri.substring(5, comma);
            String dataPart = dataUri.substring(comma + 1);

            boolean base64 = meta.contains(";base64");
            String mime = meta.contains(";") ? meta.substring(0, meta.indexOf(';')) : mimeType;
            if (mime == null || mime.isEmpty()) mime = "text/plain";

            byte[] bytes;
            if (base64) {
                bytes = Base64.decode(dataPart, Base64.DEFAULT);
            } else {
                bytes = URLDecoder.decode(dataPart, "UTF-8").getBytes(StandardCharsets.UTF_8);
            }

            File downloads = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (downloads == null) downloads = getFilesDir();

            File outDir = new File(downloads, "FlingerBit");
            if (!outDir.exists()) outDir.mkdirs();

            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
            if (ext == null || ext.isEmpty()) ext = "txt";

            File out = new File(outDir, "download_" + System.currentTimeMillis() + "." + ext);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(bytes);
            }

            binding.terminal.setText("Archivo descargado en:\n" + out.getAbsolutePath());
        } catch (Exception e) {
            binding.terminal.setText("ERROR guardando data URI:\n" + e.getMessage());
        }
    }

    private abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
    }
    }
