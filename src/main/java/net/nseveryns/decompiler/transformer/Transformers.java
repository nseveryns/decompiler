package net.nseveryns.decompiler.transformer;

import net.nseveryns.decompiler.transformer.dissambler.JavacDissasembler;

public enum Transformers {
    JAVA(new JavaDecompiler()),
    CLASS(new JavacDissasembler()),
    JAR(new JarDecompiler());

    private final Transformer transformer;

    Transformers(Transformer transformer) {
        this.transformer = transformer;
    }

    public Transformer getInstance() {
        return this.transformer;
    }
}
