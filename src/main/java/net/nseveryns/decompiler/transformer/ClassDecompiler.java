package net.nseveryns.decompiler.transformer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.nseveryns.decompiler.Project;
import net.nseveryns.decompiler.transformer.format.clazz.Attribute;
import net.nseveryns.decompiler.transformer.format.clazz.ConstantPoolReader;
import net.nseveryns.decompiler.transformer.format.clazz.FieldTable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class ClassDecompiler implements Transformer {
    private final Map<Byte, String> codes;

    public ClassDecompiler() {
        InputStream inputStream = this.getClass().getResourceAsStream("/opcodes.txt");
        Map<Byte, String> codeToString = new HashMap<>();
        try {
            List<String> lines = IOUtils.readLines(inputStream);
            for (String line : lines) {
                String code;
                byte identifier;
                try {
                    String[] split = line.split(" ");
                    code = split[0];
                    identifier = Byte.parseByte(split[1], 16);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                codeToString.put(identifier, code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.codes = ImmutableMap.copyOf(codeToString);
    }

    @Override
    public Project createProject(File file) {
        return new Project(file.getName(), ImmutableMap.of(file.getName(), file));
    }

    @Override
    public void decompile(File file, Consumer<String> consumer) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            consumer.accept(formatHex(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptFile(File file) {
        return FilenameUtils.getExtension(file.getName()).equals("class");
    }

    private String formatHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        buf.skipBytes(8); //Skip cafebabe, major and minor
        ConstantPoolReader parser = new ConstantPoolReader(buf);
        int accessBitmask = buf.readUnsignedShort();
        for (AccessFlags flags : AccessFlags.values()) {
            if ((accessBitmask & flags.flag) == 1) {
                builder.append(flags.name().toLowerCase()).append(" ");
            }
        }
        builder.append("class ");
        int identity = buf.readUnsignedShort();
        ConstantPoolReader.Entry entry = parser.getEntry(identity);
        byte[] entryBytes = entry.getBytes();
        byte high = entryBytes[0];
        byte low = entryBytes[1];
        short index= (short) ((high & 0xFF) << 8 | (low & 0xFF));
        ConstantPoolReader.Entry nameEntry = parser.getEntry(index);
        builder.append(new String(nameEntry.getBytes()));
        builder.append(" {").append("\n");
        int superIdentity = buf.readUnsignedShort();
        int interfaceCount = buf.readUnsignedShort();
        int[] interfaces = new int[interfaceCount];
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = buf.readUnsignedShort();
        }
        FieldTable fields = new FieldTable(buf);
        for (FieldTable.Field field : fields.getFields()) {
            builder.append(field.formatFlags())
                    .append(" ")
                    .append(new String(parser.getEntry(field.getNameIndex()).getBytes()))
                    .append(" ")
                    .append(new String(parser.getEntry(field.getDescriptorIndex()).getBytes()))
                    .append("\n");
        }
        //MethodTable methods = new MethodTable(buf);
        //for (MethodTable.Method method : methods.getMethods()) {
        //    builder.append(method.formatFlags())
        //            .append(new String(parser.getEntry(method.getNameIndex()+1).getBytes()))
        //            .append("\n");
        //}
        Attribute attribute = new Attribute(buf);

        builder.append("}");
        return builder.toString();
        /*
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            if (codes.containsKey(b)) {
                sb.append("\n").append(codes.get(b)).append("   ");
                continue;
            }
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
        */
    }

    enum AccessFlags {
        PUBLIC(0x0001),
        FINAL(0x0010),
        SUPER(0x0020),
        INTERFACE(0x0200),
        ABSTRACT(0x0400),
        SYNTHETIC(0x1000),
        ANNOTATION(0x2000),
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