package net.nseveryns.decompiler.transformer.format.clazz;

import io.netty.buffer.ByteBuf;

/**
 * Representation of the fields in a .class file based on JVM specifications.
 *
 * @Link http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.5
 */
public class FieldTable {
    private final Field[] fields;

    public FieldTable(ByteBuf buf) {
        fields = new Field[buf.readUnsignedShort()];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new Field(buf);
        }
    }

    public Field[] getFields() {
        return fields;
    }

    public class Field {
        private final int flags;
        private final int nameIndex;
        private final int descriptorIndex;
        private final Attribute[] attributes;

        public Field(ByteBuf buf) {
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
}
