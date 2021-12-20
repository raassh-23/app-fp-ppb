package com.raassh.simplecamera;

public class TranslationHistory {
    String sourceLang;
    String sourceText;
    String toLang;
    String toText;

    public TranslationHistory(String sourceLang, String sourceText, String toLang, String toText) {
        this.sourceLang = sourceLang;
        this.sourceText = sourceText;
        this.toLang = toLang;
        this.toText = toText;
    }

    public String getSourceLang() {
        return sourceLang.trim();
    }

    public String getSourceText() {
        return sourceText.trim();
    }

    public String getToLang() {
        return toLang.trim();
    }

    public String getToText() {
        return toText.trim();
    }

    @Override
    public String toString() {
        return "TranslationItem{" +
                "sourceLang='" + sourceLang + '\'' +
                ", sourceText='" + sourceText + '\'' +
                ", toLang='" + toLang + '\'' +
                ", toText='" + toText + '\'' +
                '}';
    }
}
