package com.licola.route.plugin

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class RouterApiTransform {

    private static final String TARGET_CLASS_PATH = "com/licola/route/api/RouterApi.class"
    private static final String TARGET_CLASS_NAME = "com.licola.route.api.RouterApi"
    private static final String TARGET_CLASS_METHOD_NAME = "loadRoute"

    private static final String TARGET_INTERFACE_NAME = "com/licola/route/api/RouteRoot"

    List<String> routeImplClassList = new ArrayList<>()

    File targetFile

    void log(String msg) {
        println("read class:" + msg)
    }

    void process() {
        if (routeImplClassList.isEmpty()) {
            return
        }

        for (String item : routeImplClassList) {
            log("router:${item}")
        }

        if (targetFile != null && targetFile.getName().endsWith(".jar")) {
            insertCode(targetFile)
        }
    }

    void insertCode(File jarFile) {
        log("targetFile:${jarFile.toString()}")
        def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
        if (optJar.exists()) {
            optJar.delete()
        }
        def file = new JarFile(jarFile)
        Enumeration<JarEntry> enumeration = file.entries()
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = file.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)

            if (TARGET_CLASS_PATH == entryName) {
                jarOutputStream.write(hackTarget(jarFile))
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()

        if (jarFile.exists()) {
            jarFile.delete()
        }

        optJar.renameTo(jarFile)
    }

    byte[] hackTarget(File jarFile) {
        ClassPool classPool = ClassPool.getDefault()
        classPool.insertClassPath(jarFile.absolutePath)

        CtClass ctClass = classPool.get(TARGET_CLASS_NAME)
        CtMethod ctMethod = ctClass.getDeclaredMethod(TARGET_CLASS_METHOD_NAME)

//        ctMethod.insertBefore("throw new RuntimeException(\"插入异常代码\");")

        for (String item : routeImplClassList) {
            //    routeRoots.add(new com.licola.route.RouteApp.Route());
            ctMethod.insertBefore(" routeRoots.add(new ${item}());")
        }

        def bytes = ctClass.toBytecode()
        ctClass.stopPruning(true)
        ctClass.defrost()

        return bytes
    }

    void readClassWithPath(File dir) {
        log("input:${dir.absolutePath}")
        def root = dir.absolutePath
        dir.eachFileRecurse { File file ->
            String filePath = file.absoluteFile
            if (!filePath.endsWith(".class")) return
            def className = getClassName(root, filePath)
            InputStream inputStream = new FileInputStream(new File(filePath))
            log("filePath:${filePath} className:${className}")
            findRouteImpl(inputStream, className)
            inputStream.close()
        }
    }

    void readClassWithJar(File input, File dest) {
        log("input:${input.absolutePath} dest:${dest}")

        JarFile jarFile = new JarFile(input)
        Enumeration<JarEntry> entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement()
            String entryName = entry.getName()
            if (!entryName.endsWith(".class")) continue
            String className = entryName.substring(0, entryName.length() - ".class".length()).replaceAll("/", ".")
            log("entryName:${entryName} className:${className}")
            InputStream inputStream = jarFile.getInputStream(entry)

            findRouteImpl(inputStream, className)

            findRouteTargetFile(entryName, dest)

            inputStream.close()
        }
    }

    boolean findRouteTargetFile(String entryName, File file) {
        if (TARGET_CLASS_PATH == entryName) {
            targetFile = file
            return true
        }

        return false
    }

    boolean findRouteImpl(InputStream inputStream, String className) {
        ClassReader reader = new ClassReader(inputStream)
        ClassNode node = new ClassNode()
        reader.accept(node, 1)
        String[] interfaces = reader.getInterfaces()

        if (interfaces == null || interfaces.length == 0) {
            return false
        }

        for (String interfaceName : interfaces) {
            if (TARGET_INTERFACE_NAME == interfaceName) {
                routeImplClassList.add(className)
                return true
            }
        }

        return false

    }

    String getClassName(String root, String classPath) {
        return classPath.substring(root.length() + "/".length(), classPath.length() - ".class".length()).replaceAll("/", ".")
    }
}