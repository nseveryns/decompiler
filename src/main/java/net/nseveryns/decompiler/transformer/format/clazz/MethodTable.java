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
        for (int i = 0; i < methods.length; i++) {
            methods[i] = new Method(buf);
        }
    }

    public Method[] getMethods() {
        return methods;
    }

    public class Method extends JavaClassEntry {

        public Method(ByteBuf buf) {
            super(buf);
        }

    }
}
