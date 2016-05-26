package net.nseveryns.decompiler.transformer.format.clazz;

import io.netty.buffer.ByteBuf;

/**
 * Container for method structures that are based on JVM specficiations.
 *
 * @Link http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6
 */
public class MethodTable {

    private final Method[] methods;

    public MethodTable(ByteBuf buf) {
        methods = new Method[buf.readUnsignedShort()];
        System.out.println("Methods: " + methods.length);
        for (int i = 0; i < methods.length; i++) {
            methods[i] = new Method(buf);
        }
    }

    public Method[] getMethods() {
        return methods;
    }

    public class Method {
        private final int flags;
        private final int nameIndex;
        private final int descriptorIndex;
        private final Attribute[] attributes;

        public Method(ByteBuf buf) {
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
            System.out.println("Flags: " + this.flags);
            for (MethodFlags methodFlags : MethodFlags.values()) {
                if ((this.flags & methodFlags.flag) == 1) {
                    builder.append(methodFlags.name().toLowerCase());
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

    private enum MethodFlags {
        PUBLIC(0x0001),
        PRIVATE(0x0002),
        PROTECTED(0x0004),
        STATIC(0x0008),
        FINAL(0x0010),
        SYNCHRONIZED(0x0020),
        BRIDGE(0x0040),
        VARARGS(0x0080),
        NATIVE(0x0100),
        ABSTRACT(0x0400),
        STRICTFP(0x0800),
        SYNTHETIC(0x1000);

        private final int flag;

        MethodFlags(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }
    }
}
