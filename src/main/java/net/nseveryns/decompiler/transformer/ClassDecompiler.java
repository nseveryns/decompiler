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
import net.nseveryns.decompiler.transformer.format.clazz.*;
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
            consumer.accept(readToCode(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptFile(File file) {
        return FilenameUtils.getExtension(file.getName()).equals("class");
    }

    private String readToCode(byte[] bytes) {
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        buf.skipBytes(8); //Skip cafebabe, major and minor
        ConstantPoolTable parser = new ConstantPoolTable(buf);
        int accessBitmask = buf.readUnsignedShort();
        int identity = buf.readUnsignedShort();
        int superIdentity = buf.readUnsignedShort();
        int interfaceCount = buf.readUnsignedShort();
        int[] interfaces = new int[interfaceCount];
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = buf.readUnsignedShort();
        }
        FieldTable fields = new FieldTable(buf);

        MethodTable methods = new MethodTable(buf);
        //for (MethodTable.Method method : methods.getMethods()) {
        //    builder.append(method.formatFlags())
        //            .append(new String(parser.getEntry(method.getNameIndex()+1).getBytes()))
        //            .append("\n");
        //}

        return new JavaFormatter(accessBitmask, identity, parser, fields, methods).format();
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
}