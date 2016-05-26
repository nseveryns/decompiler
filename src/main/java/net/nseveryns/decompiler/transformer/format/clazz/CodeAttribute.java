package net.nseveryns.decompiler.transformer.format.clazz;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CodeAttribute {
    private final int maxStack;
    private final int maxLocals;
    private final byte[] code;

    public CodeAttribute(Attribute attribute) {
        ByteBuf buf = Unpooled.copiedBuffer(attribute.getInfo());
        maxStack = buf.readUnsignedShort();
        maxLocals = buf.readUnsignedShort();
        int length = buf.readInt();
        this.code = new byte[length];
        buf.readBytes(this.code);
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
}
