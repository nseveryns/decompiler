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
            this.attributes = new Attribute[buf.readUnsignedShort()];
        }

        public int getFlags() {
            return flags;
        }

        public String formatFlags() {
            StringBuilder builder = new StringBuilder();
            for (AccessFlags accessFlags : AccessFlags.values()) {
                if ((accessFlags.getFlag() & getFlags()) == 1) {
                    builder.append(accessFlags.name().toLowerCase());
                }
            }
            return builder.toString();
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

    enum AccessFlags {
        PUBLIC(0x0001),
        PRIVATE(0x0002),
        PROTECTED(0x0004),
        STATIC(0x0008),
        FINAL(0x0010),
        VOLATILE(0x0040),
        TRANSIENT(0x0080),
        SYNTHETIC(0x1000),
        ENUM(0x4000);

        private final int flag;

        AccessFlags(int flag) {

            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }
    }
}
