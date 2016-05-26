package net.nseveryns.decompiler.transformer.format.clazz;


import io.netty.buffer.ByteBuf;

/**
 * Attributes that are found in the .class format
 *
 * @link http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7
 */
public class Attribute {
    private final int attributeNameIndex;
    private final byte[] info;

    public Attribute(ByteBuf buf) {
        this.attributeNameIndex = buf.readUnsignedShort();
        this.info = new byte[buf.readInt()];
    }

    public int getAttributeNameIndex() {
        return attributeNameIndex;
    }

    public byte[] getInfo() {
        return info;
    }
}