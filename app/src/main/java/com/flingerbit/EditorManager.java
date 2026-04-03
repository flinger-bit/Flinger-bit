package com.flingerbit;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public final class EditorManager {

    public interface OnSuggestionTap {
        void onTap(String insertText);
    }

    private final Context context;
    private final SuggestionEngine suggestionEngine = new SuggestionEngine();
    private final CodeHighlighter highlighter;

    private final List<SuggestionEngine.Suggestion> items = new ArrayList<>();
    private SuggestionAdapter adapter;
    private EditText editor;
    private String fileName = "example.html";
    private boolean internalChange = false;

    public EditorManager(Context context, int keywordColor, int tagColor, int stringColor, int commentColor) {
        this.context = context.getApplicationContext();
        this.highlighter = new CodeHighlighter(keywordColor, tagColor, stringColor, commentColor);
    }

    public void attach(EditText editor, RecyclerView suggestionList, OnSuggestionTap onSuggestionTap) {
        this.editor = editor;

        suggestionList.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SuggestionAdapter(items, onSuggestionTap);
        suggestionList.setAdapter(adapter);

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (internalChange) return;
                refresh(s.toString(), editor.getSelectionStart());
            }
        });

        refresh(editor.getText() == null ? "" : editor.getText().toString(), editor.getSelectionStart());
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? "" : fileName;
        if (editor != null) {
            refresh(getText(), editor.getSelectionStart());
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getText() {
        if (editor == null || editor.getText() == null) return "";
        return editor.getText().toString();
    }

    public void setText(String text) {
        if (editor == null) return;
        internalChange = true;
        editor.setText(text);
        if (editor.getText() != null) {
            editor.setSelection(Math.min(editor.length(), editor.getText().length()));
        }
        internalChange = false;
        refresh(text == null ? "" : text, editor.getSelectionStart());
    }

    private void refresh(String text, int cursor) {
        if (editor == null) return;
        LanguageSpec spec = LanguageSpec.forFile(fileName);
        List<SuggestionEngine.Suggestion> suggestions = suggestionEngine.suggest(spec, text, cursor);

        items.clear();
        items.addAll(suggestions);
        if (adapter != null) adapter.notifyDataSetChanged();

        CharSequence highlighted = highlighter.highlight(spec, text);
        internalChange = true;
        int selection = editor.getSelectionStart();
        editor.setText(highlighted);
        editor.setSelection(Math.min(selection, editor.length()));
        internalChange = false;
    }

    private static final class SuggestionAdapter extends RecyclerView.Adapter<SuggestionHolder> {
        private final List<SuggestionEngine.Suggestion> data;
        private final OnSuggestionTap onSuggestionTap;

        SuggestionAdapter(List<SuggestionEngine.Suggestion> data, OnSuggestionTap onSuggestionTap) {
            this.data = data;
            this.onSuggestionTap = onSuggestionTap;
        }

        @Override
        public SuggestionHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_suggestion, parent, false);
            return new SuggestionHolder(view);
        }

        @Override
        public void onBindViewHolder(SuggestionHolder holder, int position) {
            SuggestionEngine.Suggestion item = data.get(position);
            holder.title.setText(item.title);
            holder.subtitle.setText(item.subtitle);
            holder.itemView.setOnClickListener(v -> {
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
        final TextView title;
        final TextView subtitle;

        SuggestionHolder(android.view.View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }
}
