package com.flingerbit;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flingerbit.databinding.ItemSuggestionBinding;

import java.util.ArrayList;
import java.util.List;

public final class EditorManager {

    public interface OnSuggestionTap {
        void onTap(String insertText);
    }

    private final SuggestionEngine suggestionEngine = new SuggestionEngine();
    private final List<SuggestionEngine.Suggestion> items = new ArrayList<>();

    private EditText editor;
    private String fileName = "index.html";
    private boolean internalUpdate = false;
    private SuggestionsAdapter adapter;

    public void attach(EditText editor, RecyclerView suggestionList, OnSuggestionTap onSuggestionTap) {
        this.editor = editor;

        suggestionList.setLayoutManager(new LinearLayoutManager(editor.getContext()));
        adapter = new SuggestionsAdapter(items, onSuggestionTap);
        suggestionList.setAdapter(adapter);

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!internalUpdate) {
                    refreshSuggestions();
                }
            }
        });

        refreshSuggestions();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? "" : fileName.trim();
        refreshSuggestions();
    }

    public String getFileName() {
        return fileName;
    }

    public String getText() {
        if (editor == null || editor.getText() == null) {
            return "";
        }
        return editor.getText().toString();
    }

    public void setText(String text) {
        if (editor == null) {
            return;
        }
        internalUpdate = true;
        editor.setText(text == null ? "" : text);
        editor.setSelection(editor.length());
        internalUpdate = false;
        refreshSuggestions();
    }

    public void insertText(String insertion) {
        if (editor == null || insertion == null) {
            return;
        }
        Editable editable = editor.getText();
        if (editable == null) {
            return;
        }
        int start = Math.max(0, editor.getSelectionStart());
        int end = Math.max(0, editor.getSelectionEnd());
        editable.replace(Math.min(start, end), Math.max(start, end), insertion);
    }

    private void refreshSuggestions() {
        if (editor == null || adapter == null) {
            return;
        }
        int cursor = Math.max(0, editor.getSelectionStart());
        List<SuggestionEngine.Suggestion> suggestions = suggestionEngine.suggest(fileName, getText(), cursor);
        items.clear();
        items.addAll(suggestions);
        adapter.notifyDataSetChanged();
    }

    private static final class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionHolder> {
        private final List<SuggestionEngine.Suggestion> data;
        private final OnSuggestionTap onSuggestionTap;

        SuggestionsAdapter(List<SuggestionEngine.Suggestion> data, OnSuggestionTap onSuggestionTap) {
            this.data = data;
            this.onSuggestionTap = onSuggestionTap;
        }

        @Override
        public SuggestionHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            ItemSuggestionBinding binding = ItemSuggestionBinding.inflate(
                    android.view.LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            );
            return new SuggestionHolder(binding);
        }

        @Override
        public void onBindViewHolder(SuggestionHolder holder, int position) {
            SuggestionEngine.Suggestion item = data.get(position);
            holder.binding.title.setText(item.title);
            holder.binding.subtitle.setText(item.subtitle);
            holder.binding.getRoot().setOnClickListener(v -> {
                if (onSuggestionTap != null && item.insertText != null && !item.insertText.isEmpty()) {
                    onSuggestionTap.onTap(item.insertText);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private static final class SuggestionHolder extends RecyclerView.ViewHolder {
        final ItemSuggestionBinding binding;

        SuggestionHolder(ItemSuggestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
