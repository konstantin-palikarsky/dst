package dst.ass2.ioc.lock;

import dst.ass2.ioc.di.annotation.Component;
import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.stream.Collectors;


public class LockingInjector implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        // TODO transform all @Lock annotated methods of classes with a @Component annotation
        try {
            byte[] byteCode = classfileBuffer;

            ClassPool pool = ClassPool.getDefault();
            String normalizedClassName = className.replaceAll("/", ".");
            CtClass ctClass = pool.get(normalizedClassName);

            if (!ctClass.hasAnnotation(Component.class)) {
                return byteCode;
            }

            var lockedMethods = Arrays
                    .stream(ctClass.getDeclaredMethods())
                    .filter(x -> x.hasAnnotation(Lock.class))
                    .collect(Collectors.toList());
            if (lockedMethods.isEmpty()) {
                //Early termination
                return byteCode;
            }


            lockedMethods.forEach(this::addLockingToMethod);

            ctClass.writeFile();
            byteCode = ctClass.toBytecode();
            ctClass.detach();

            return byteCode;

        } catch (NotFoundException | IOException | CannotCompileException e) {

            throw new RuntimeException("Error adding locking to " + className + ":" + e.getMessage());
        }
    }

    private void addLockingToMethod(CtMethod method) {
        System.err.println("Pretend I transformed a method");
        try {
            /*
            var methodInfo = method.getMethodInfo2();
            var attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
            var an = attr.getAnnotation("Lock");
            var lockName = ((StringMemberValue) an.getMemberValue("value")).getValue();
            */

            var lockName = ((Lock) method.getAnnotation(Lock.class)).value();
            System.err.println("Trying to get lock: " + lockName + "for " + method.getName());

            method.insertBefore("dst.ass2.ioc.lock.LockManager.lock(\"" + lockName + "\");");
            method.insertAfter("dst.ass2.ioc.lock.LockManager.unlock(\"" + lockName + "\");", true);

        } catch (CannotCompileException e) {
            throw new RuntimeException("Error adding locking to " + method.getName() + ":" + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
