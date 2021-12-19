package com.raassh.simplecamera;

public class Foto {
    private String name;
    private String link;
    private String text;
    private String language;

    public Foto(String name, String link, String text, String language) {
        this.name = name;
        this.link = link;
        this.text = text;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getText() { return text; }

    public String getLanguage() { return language; }

    @Override
    public String toString() {
        return "Foto{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", text='" + text + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
