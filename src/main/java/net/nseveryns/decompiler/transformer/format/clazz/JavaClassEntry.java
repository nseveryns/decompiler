package net.nseveryns.decompiler.transformer.format.clazz;

import io.netty.buffer.ByteBuf;

/**
 * @author nseveryns
 */
public abstract class JavaClassEntry {
    private final int flags;
    private final int nameIndex;
    private final int descriptorIndex;
    private final Attribute[] attributes;

    public JavaClassEntry(ByteBuf buf) {
        this.flags = buf.readUnsignedShort();
        this.nameIndex = buf.readUnsignedShort();
        this.descriptorIndex = buf.readUnsignedShort();
        int length = buf.readUnsignedShort();
        this.attributes = new Attribute[length];
        for (int i = 0; i < this.attributes.length; i++) {
            attributes[i] = new Attribute(buf);
        }
    }

    public int getFlags() {
        return flags;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }
}
