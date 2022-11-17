package com.holland.kit.base.asm;


import com.holland.kit.base.file.FileKit;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        ClassReader   classReader        = new ClassReader("com.holland.kit.base.asm.A");
        ClassWriter           classWriter        = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        DeepCloneClassVisitor metricClassVisitor = new DeepCloneClassVisitor(Opcodes.ASM9, classWriter);
        classReader.accept(metricClassVisitor, ClassReader.SKIP_FRAMES);
        byte[] bytes = classWriter.toByteArray();
        FileKit.write(".", "A.class", false, new String(bytes));
    }
}
