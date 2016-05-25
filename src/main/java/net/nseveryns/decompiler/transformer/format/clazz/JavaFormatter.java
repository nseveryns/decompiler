package net.nseveryns.decompiler.transformer.format.clazz;

import org.apache.commons.io.FilenameUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author nseveryns
 */
public class JavaFormatter {
    private final int accessBitmask;
    private final int identity;
    private final ConstantPoolTable constants;
    private final FieldTable fields;
    private final MethodTable methods;
    private final StringBuilder builder;
    private final Set<String> imports;
    private int packageEndIndex;

    public JavaFormatter(int access, int identity, ConstantPoolTable constants, FieldTable fields, MethodTable methods) {
        this.accessBitmask = access;
        this.identity = identity;
        this.constants = constants;
        this.fields = fields;
        this.methods = methods;
        this.builder = new StringBuilder();
        this.imports = new HashSet<>();
        this.addHeader();
        addFields();
        this.finish();
    }

    private void addImport(String importPath) {
        if (this.imports.add(importPath)) {
            this.builder.insert(packageEndIndex, "\nimport " + importPath.replace("/", ".") + ";\n\n");
        }
    }

    private void addHeader() {
        for (ClassAccessFlags flags : ClassAccessFlags.values()) {
            if ((accessBitmask & flags.flag) == flags.flag) {
                builder.append(flags.name().toLowerCase()).append(" ");
            }
        }
        builder.append("class ");
        String classPath = readString(constants.getEntry(getShort(constants.getEntry(identity))));
        String path = FilenameUtils.getPath(classPath);
        this.packageEndIndex =  path.length() + 9;
        builder.insert(0, "package " + path.replace("/", ".").substring(0, path.length() - 1) + ";" + "\n");
        builder.append(FilenameUtils.getName(classPath));
        builder.append(" {\n");
    }

    private void addFields() {
        for (FieldTable.Field field : this.fields.getFields()) {
            this.addField(field);
        }
    }

    private void addField(FieldTable.Field field) {
        builder.append("    ");
        for (FieldAccessFlags flags : FieldAccessFlags.values()) {
            if ((field.getFlags() & flags.flag) == flags.flag) {
                builder.append(flags.name().toLowerCase()).append(" ");
            }
        }
        String type = readString(constants.getEntry(field.getDescriptorIndex()));
        String substring = type.substring(1, type.length()-1);
        String className = FilenameUtils.getName(substring);
        addImport(FilenameUtils.getPath(substring) + className);
        builder.append(className).append(" ");
        builder.append(readString(constants.getEntry(field.getNameIndex()))).append(";").append("\n");
    }

    private void finish() {
        builder.append("}");
    }

    public String format() {
        return builder.toString();
    }


    private short getShort(ConstantPoolTable.Entry entry) {
        byte[] bytes = entry.getBytes();
        return getShort(bytes[0], bytes[1]);
    }

    private short getShort(byte high, byte low) {
        return (short) ((high & 0xFF) << 8 | (low & 0xFF));
    }

    private String readString(ConstantPoolTable.Entry entry) {
        return readString(entry.getBytes());
    }

    private String readString(byte[] bytes) {
        return new String(bytes);
    }

    private enum ClassAccessFlags {
        PUBLIC(0x0001),
        FINAL(0x0010),
        SUPER(0x0020),
        INTERFACE(0x0200),
        ABSTRACT(0x0400),
        SYNTHETIC(0x1000),
        ANNOTATION(0x2000),
        ENUM(0x4000);

        private final int flag;

        ClassAccessFlags(int flag) {

            this.flag = flag;
        }

        public int getFlag() {
            return flag;
        }
    }

    private enum FieldAccessFlags {
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

        FieldAccessFlags(int flag) {
            this.flag = flag;
        }
        public int getFlag() {
            return flag;
        }
    }
}