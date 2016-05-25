package net.nseveryns.decompiler.transformer.format.clazz;


import io.netty.buffer.ByteBuf;

public class Attribute {
    private final int attributeNameIndex;
    private final byte[] info;

    public Attribute(ByteBuf buf) {
        this.attributeNameIndex = buf.readUnsignedShort();
        System.out.println("Attribute:" + attributeNameIndex);
        this.info = new byte[buf.readInt()];
    }

    public int getAttributeNameIndex() {
        return attributeNameIndex;
    }

    public byte[] getInfo() {
        return info;
    }
}