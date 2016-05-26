package net.nseveryns.decompiler.transformer;

import java.io.File;
import java.util.function.Consumer;

import net.nseveryns.decompiler.Project;

/**
 *
 * The interface that all transformers must implement.
 *
 */
public interface Transformer {

    /**
     * Creates a project representation of a file.
     *
     * @see Project
     * @param file the file to decompile or put into a project
     * @return project representation of file.
     */
    Project createProject(File file);

    /**
     * Decompiles the selected file into a textual representation and then will
     * call the consumer for that class. This is completely thread safe.
     *
     * @param file the file to decompile into a textual representation.
     * @param consumer action to be performed after the file has been completed.
     */
    void decompile(File file, Consumer<String> consumer);

    /**
     * Checks wether the specific transformer is able to use the file.
     *
     * @param file the file to attempt decompilation.
     * @return if the file is compatible
     */
    boolean acceptFile(File file);

}
