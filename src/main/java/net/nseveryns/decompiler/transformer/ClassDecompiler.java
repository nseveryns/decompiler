package net.nseveryns.decompiler.transformer;

import com.google.common.collect.ImmutableMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.nseveryns.decompiler.Project;
import net.nseveryns.decompiler.transformer.format.clazz.ConstantPoolTable;
import net.nseveryns.decompiler.transformer.format.clazz.FieldTable;
import net.nseveryns.decompiler.transformer.format.clazz.InstructionSet;
import net.nseveryns.decompiler.transformer.format.clazz.JavaFormatter;
import net.nseveryns.decompiler.transformer.format.clazz.MethodTable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ClassDecompiler implements Transformer {
    private final Map<Integer, InstructionSet> codes;

    public ClassDecompiler() {
        InputStream inputStream = this.getClass().getResourceAsStream("/opcodes.txt");
        Map<Integer, InstructionSet> codeToString = new HashMap<>();
        try {
            List<String> lines = IOUtils.readLines(inputStream);
            for (String line : lines) {
                String code;
                int identifier;
                InstructionSet instruction;
                try {
                    String[] split = line.split(" ");
                    code = split[0];
                    identifier = Integer.decode(split[1]);
                    instruction = new InstructionSet(identifier, code, split.length > 2 ? Integer.parseInt(split[2]) : 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                codeToString.put(identifier, instruction);
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
        return new JavaFormatter(accessBitmask, identity, superIdentity, parser, interfaces, fields, methods, codes).format();
    }
}