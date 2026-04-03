package com.flingerbit;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CodeHighlighter {

    private final int keywordColor;
    private final int tagColor;
    private final int stringColor;
    private final int commentColor;

    public CodeHighlighter(int keywordColor, int tagColor, int stringColor, int commentColor) {
        this.keywordColor = keywordColor;
        this.tagColor = tagColor;
        this.stringColor = stringColor;
        this.commentColor = commentColor;
    }

    public CharSequence highlight(LanguageSpec spec, String text) {
        if (text == null) return "";
        SpannableStringBuilder sb = new SpannableStringBuilder(text);

        if (spec != null && "html".equals(spec.extension)) {
            applyPattern(sb, Pattern.compile("<!--(.*?)-->", Pattern.DOTALL), commentColor);
            applyPattern(sb, Pattern.compile("</?\\w+[^>]*>"), tagColor);
            applyPattern(sb, Pattern.compile("\"[^\"]*\""), stringColor);
            applyPattern(sb, Pattern.compile("'[^']*'"), stringColor);
            return sb;
        }

        if (spec != null && "css".equals(spec.extension)) {
            applyPattern(sb, Pattern.compile("/\\*(.*?)\\*/", Pattern.DOTALL), commentColor);
            applyPattern(sb, Pattern.compile("\"[^\"]*\""), stringColor);
            applyPattern(sb, Pattern.compile("'[^']*'"), stringColor);
            return sb;
        }

        if (spec != null) {
            List<String> keywords = spec.keywords;
            for (String kw : keywords) {
                applyWholeWord(sb, kw, keywordColor);
            }
        }

        applyPattern(sb, Pattern.compile("//.*?$", Pattern.MULTILINE), commentColor);
        applyPattern(sb, Pattern.compile("/\\*(.*?)\\*/", Pattern.DOTALL), commentColor);
        applyPattern(sb, Pattern.compile("\"[^\"]*\""), stringColor);
        applyPattern(sb, Pattern.compile("'[^']*'"), stringColor);

        return sb;
    }

    private static void applyPattern(SpannableStringBuilder sb, Pattern pattern, int color) {
        Matcher matcher = pattern.matcher(sb);
        while (matcher.find()) {
            sb.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void applyWholeWord(SpannableStringBuilder sb, String word, int color) {
        Pattern p = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
        applyPattern(sb, p, color);
    }
}
