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

    public class Field extends JavaClassEntry {

        public Field(ByteBuf buf) {
            super(buf);
        }
    }
}
