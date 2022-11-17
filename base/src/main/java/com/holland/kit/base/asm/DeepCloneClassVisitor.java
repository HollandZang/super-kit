package com.holland.kit.base.asm;

import org.objectweb.asm.*;

public class DeepCloneClassVisitor extends ClassVisitor {
    protected DeepCloneClassVisitor(int api) {
        super(api);
    }

    protected DeepCloneClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        for (String anInterface : interfaces) {
            if ("com/holland/kit/base/asm/DeepClone".equals(anInterface)) {
//                MethodVisitor deepClone = cv.visitMethod(access, name + "1", "deepClone", signature, null);
//                //访问需要修改的方法
//                MethodVisitor newMethod = new MethodVisitor(this.api, deepClone) {
//                    @Override
//                    public MethodVisitor getDelegate() {
//                        return super.getDelegate();
//                    }
//                };
//                ((ClassWriter) this.cv).newMethod("", "test", "test", false);
            }
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if ("print".equals(name)) {
            MethodVisitor deepClone = cv.visitMethod(access, name, "(Ljava/lang/String;)V", signature, null);
            return deepClone;
        }
        return methodVisitor;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }
}
