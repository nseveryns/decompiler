package net.nseveryns.decompiler.transformer.format.clazz;

public class InstructionSet {
    private final int code;
    private final String name;
    private final int extra;

    public InstructionSet(int code, String name, int extra) {
        this.code = code;
        this.name = name;
        this.extra = extra;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getExtra() {
        return extra;
    }
}
