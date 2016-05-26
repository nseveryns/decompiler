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
            int length = buf.readUnsignedShort();
            System.out.println("There are " + length + " attributes in one method.");
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
