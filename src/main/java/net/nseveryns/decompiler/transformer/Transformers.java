package net.nseveryns.decompiler.transformer;

public enum Transformers {
    JAVA(new JavaDecompiler()),
    CLASS(new ClassDecompiler()),
    JAR(new JarDecompiler());

    private final Transformer transformer;

    Transformers(Transformer transformer) {
        this.transformer = transformer;
    }

    public Transformer getInstance() {
        return this.transformer;
    }
}
