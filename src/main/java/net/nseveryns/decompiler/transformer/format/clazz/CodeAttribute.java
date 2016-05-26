package net.nseveryns.decompiler.transformer.format.clazz;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CodeAttribute {
    private final int maxStack;
    private final int maxLocals;
    private final byte[] code;
    private final byte[] exceptionTable;
    private final Attribute[] attributes;

    public CodeAttribute(Attribute attribute) {
        ByteBuf buf = Unpooled.copiedBuffer(attribute.getInfo());
        maxStack = buf.readUnsignedShort();
        maxLocals = buf.readUnsignedShort();
        int length = buf.readInt();
        this.code = new byte[length];
        buf.readBytes(this.code);
        this.exceptionTable = new byte[buf.readUnsignedShort() * 8];
        buf.readBytes(exceptionTable);
        this.attributes = new Attribute[buf.readUnsignedShort()];
        for (int i = 0; i < attributes.length; i++) {
            attributes[i] = new Attribute(buf);
        }
        buf.release();
    }

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public byte[] getCode() {
        return code;
    }

    public byte[] getExceptionTable() {
        return exceptionTable;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }
}
