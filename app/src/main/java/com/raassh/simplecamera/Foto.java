package com.raassh.simplecamera;

public class Foto {
    private String name;
    private String link;
    private String base64;

    public Foto(String name, String link, String base64) {
        this.name = name;
        this.link = link;
        this.base64 = base64;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getBase64() {
        return base64;
    }
}
