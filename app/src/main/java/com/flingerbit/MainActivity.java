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
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flingerbit.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FileManager fileManager;
    private EditorManager editorManager;
    private String currentFileName = "index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        fileManager = new FileManager(this);
        editorManager = new EditorManager();

        setupWebView();
        setupEditor();

        binding.fileName.setText(currentFileName);
        editorManager.setFileName(currentFileName);
        editorManager.setText(fileManager.templateFor(currentFileName));

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

        binding.btnNew.setOnClickListener(v -> newFile());
        binding.btnSave.setOnClickListener(v -> saveCurrentFile());
        binding.btnRun.setOnClickListener(v -> runCurrentFile());

        binding.terminal.setText(getString(R.string.ready_message));
    }

    private void setupEditor() {
        editorManager.attach(binding.codeEditor, binding.suggestionList, insertion -> {
            replaceSelection(binding.codeEditor, insertion);
        });
    }

    private void setupWebView() {
        WebSettings settings = binding.previewWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        binding.previewWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                binding.terminal.setText(getString(R.string.preview_ready));
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                binding.terminal.setText("WebView error: " + error.getDescription());
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
        currentFileName = safeFileName(binding.fileName.getText() == null ? "" : binding.fileName.getText().toString().trim());
        if (currentFileName.isEmpty()) {
            currentFileName = "index.html";
        }

        binding.fileName.setText(currentFileName);
        editorManager.setFileName(currentFileName);
        editorManager.setText(fileManager.templateFor(currentFileName));
        binding.previewWebView.setVisibility(android.view.View.GONE);
        binding.terminal.setText(getString(R.string.file_created));
    }

    private void saveCurrentFile() {
        try {
            currentFileName = safeFileName(binding.fileName.getText() == null ? "" : binding.fileName.getText().toString().trim());
            if (currentFileName.isEmpty()) {
                currentFileName = "example.txt";
            }

            editorManager.setFileName(currentFileName);
            fileManager.save(currentFileName, editorManager.getText());

            File saved = fileManager.resolve(currentFileName);
            binding.terminal.setText("Guardado en:\n" + saved.getAbsolutePath());
            Toast.makeText(this, R.string.file_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            binding.terminal.setText("ERROR guardando:\n" + e.getMessage());
        }
    }

    private void runCurrentFile() {
        currentFileName = safeFileName(binding.fileName.getText() == null ? "" : binding.fileName.getText().toString().trim());
        if (currentFileName.isEmpty()) {
            currentFileName = "index.html";
        }

        String code = editorManager.getText();
        String lower = currentFileName.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".html") || lower.endsWith(".htm") || lower.endsWith(".css") || lower.endsWith(".js")) {
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

        binding.previewWebView.setVisibility(android.view.View.GONE);
        binding.terminal.setText(
                "Lenguaje detectado: " + currentFileName + "\n" +
                        "La ejecución nativa para este lenguaje aún no está integrada.\n" +
                        "El archivo sí se puede guardar y exportar."
        );
    }

    private void replaceSelection(EditText editor, String insertion) {
        if (editor == null || insertion == null) {
            return;
        }

        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        editor.getText().replace(Math.min(start, end), Math.max(start, end), insertion);
    }

    private void handleDownload(String url, String userAgent, String contentDisposition, String mimeType) {
        try {
            String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
            if (fileName == null || fileName.isEmpty()) {
                fileName = "download_" + System.currentTimeMillis();
            }

            if (url.startsWith("data:")) {
                saveDataUri(url, fileName, mimeType);
                return;
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(fileName);
            request.setDescription("Descargando desde Flinger-Bit");
            request.addRequestHeader("User-Agent", userAgent);
            if (mimeType != null) {
                request.setMimeType(mimeType);
            }
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                binding.terminal.setText("Descarga iniciada: " + fileName);
            } else {
                binding.terminal.setText("DownloadManager no disponible.");
            }
        } catch (Exception e) {
            binding.terminal.setText("ERROR de descarga:\n" + e.getMessage());
        }
    }

    private void saveDataUri(String dataUri, String fileName, String mimeType) {
        try {
            int comma = dataUri.indexOf(',');
            if (comma < 0) {
                throw new IllegalArgumentException("Data URI inválida");
            }

            String meta = dataUri.substring(5, comma);
            String dataPart = dataUri.substring(comma + 1);
            boolean base64 = meta.contains(";base64");

            byte[] bytes;
            if (base64) {
                bytes = Base64.decode(dataPart, Base64.DEFAULT);
            } else {
                bytes = URLDecoder.decode(dataPart, StandardCharsets.UTF_8.name()).getBytes(StandardCharsets.UTF_8);
            }

            File downloads = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (downloads == null) {
                downloads = getFilesDir();
            }

            File outDir = new File(downloads, "FlingerBit");
            if (!outDir.exists()) {
                outDir.mkdirs();
            }

            String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            if (ext == null || ext.isEmpty()) {
                ext = "txt";
            }

            File out = new File(outDir, fileName.contains(".") ? fileName : fileName + "." + ext);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(bytes);
            }

            binding.terminal.setText("Archivo descargado en:\n" + out.getAbsolutePath());
        } catch (Exception e) {
            binding.terminal.setText("ERROR guardando data URI:\n" + e.getMessage());
        }
    }

    private String safeFileName(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new) {
            newFile();
            return true;
        } else if (id == R.id.action_save) {
            saveCurrentFile();
            return true;
        } else if (id == R.id.action_run) {
            runCurrentFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    }
}
