package net.nseveryns.decompiler.transformer.format.clazz;

import io.netty.buffer.ByteBuf;

import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Constant pool reader and data holder based on the JVM specifications.
 *
 * @link http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4
 */
public class ConstantPoolTable {
    private final int count;
    private final Entry[] entries;

    public ConstantPoolTable(ByteBuf buf) {
        this.count = buf.readUnsignedShort();
        this.entries = new Entry[count-1];
        for (int i = 0; i < this.entries.length; i++) {
            this.entries[i] = new Entry(buf);
        }
    }

    public Entry getEntry(int index) {
        if (index == 0) {
            index++;
        }
        return this.entries[index - 1];
    }

    public class Entry {
        private final ConstantTagType type;
        private final byte[] bytes;

        public Entry(ByteBuf buf) {
            this.type = ConstantTagType.getFromTag(buf.readByte());
            this.bytes = this.type != null ? this.type.getBytes(buf) : new byte[0];
        }

        public ConstantTagType getType() {
            return type;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    enum ConstantTagType {
        STRING(1, buf -> {
            byte[] bytes = new byte[buf.readShort()];
            buf.readBytes(bytes);
            return ArrayUtils.toObject(bytes);
        }),
        INTEGER(3, 4),
        FLOAT(4, 4),
        LONG(5, 8),
        DOUBLE(6, 8),
        CLASS(7, 2),
        STRING_REFERENCE(8, 2),
        FIELD_REFERENCE(9, 4),
        METHOD_REFERENCE(10, 4),
        INTERFACE_METHOD_REFERENCE(11, 4),
        NAME_AND_TYPE_DESCRIPTOR(12, 4),
        METHOD_HANDLE(15, 3),
        METHOD_TYPE(16, 2),
        INVOKE_DYNAMIC(18, 4);

        private final int tag;
        private final Function<ByteBuf, Byte[]> read;

        ConstantTagType(int tag, int readAmount) {
            this(tag, buf -> {
                byte[] bytes = new byte[readAmount];
                buf.readBytes(bytes);
                return ArrayUtils.toObject(bytes);
            });
        }

        ConstantTagType(int tag, Function<ByteBuf, Byte[]> read) {
            this.tag = tag;
            this.read = read;
        }

        public byte[] getBytes(ByteBuf buf) {
            return ArrayUtils.toPrimitive(read.apply(buf));
        }

        public static ConstantTagType getFromTag(int tag) {
            for (ConstantTagType type : values()) { //TODO: Put into map
                if (type.tag == tag) {
                    return type;
                }
            }
            return null;
        }
    }
}
