package com.flingerbit;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
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
        editorManager.attach(binding.codeEditor, binding.suggestionList, insertion ->
                replaceSelection(binding.codeEditor, insertion));
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
    }

    private void newFile() {
        currentFileName = safeFileName(binding.fileName.getText() == null ? "" : binding.fileName.getText().toString().trim());
        if (currentFileName.isEmpty()) {
            currentFileName = "index.html";
        }

        binding.fileName.setText(currentFileName);
        editorManager.setFileName(currentFileName);
        editorManager.setText(fileManager.templateFor(currentFileName));
        binding.previewWebView.setVisibility(View.GONE);
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
        LanguageSpec spec = LanguageSpec.fromFileName(currentFileName);

        if (spec == LanguageSpec.HTML || spec == LanguageSpec.HTM) {
            binding.previewWebView.setVisibility(View.VISIBLE);
            binding.previewWebView.loadDataWithBaseURL(
                    "https://flingerbit.local/",
                    code,
                    "text/html",
                    "UTF-8",
                    null
            );
            binding.terminal.setText("Preview ejecutada para " + currentFileName);
            return;
        }

        if (spec == LanguageSpec.CSS || spec == LanguageSpec.JAVASCRIPT) {
            binding.previewWebView.setVisibility(View.VISIBLE);
            binding.previewWebView.loadDataWithBaseURL(
                    "https://flingerbit.local/",
                    fileManager.buildPreviewHtml(currentFileName, code),
                    "text/html",
                    "UTF-8",
                    null
            );
            binding.terminal.setText("Preview ejecutada para " + currentFileName);
            return;
        }

        binding.previewWebView.setVisibility(View.GONE);
        binding.terminal.setText(
                "Lenguaje detectado: " + spec.displayName() + "\n"
                        + "La ejecución nativa para este lenguaje aún no está integrada.\n"
                        + "El archivo sí se puede guardar y exportar."
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

    private String safeFileName(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
