package com.github.willyancaetano.pix.keys.manager.enumeration;

public enum Resources {

    KEYS("http://localhost:8080/v1/account/keys");

    private final String pathErrorResolveResources;

    Resources(final String pathErrorResolveResources) {
        this.pathErrorResolveResources = pathErrorResolveResources;
    }

    public String getPathErrorResolveResources() {
        return pathErrorResolveResources;
    }
}