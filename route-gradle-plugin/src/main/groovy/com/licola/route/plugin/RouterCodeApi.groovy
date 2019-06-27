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

/**
 * 核心的代码处理类
 */
class RouterCodeApi {

    private static final boolean DEBUG = true

    private static final String TARGET_CLASS_PATH = "com/licola/route/api/RouterApi\$Builder.class"
    private static final String TARGET_CLASS_NAME = "com.licola.route.api.RouterApi\$Builder"
    private static final String TARGET_CLASS_METHOD_NAME = "build"
    private static final String TARGET_INVOKE_METHOD_NAME = "addRouteRoot"

    private static final String TARGET_INTERFACE_NAME = "com/licola/route/api/RouteRoot"

    HashMap<String, File> routeImplInfoMap = new HashMap<>()

    File targetApiFile

    void log(String msg) {
        if (DEBUG) {
            println("api: " + msg)
        }
    }

    void process() {
        if (routeImplInfoMap.isEmpty()) {
            return
        }

        log("router:${routeImplInfoMap.toMapString()}")

        if (targetApiFile != null && targetApiFile.getName().endsWith(".jar")) {
            insertCode(targetApiFile)
        }
    }

    void insertCode(File jarFile) {
        log("insercode targetApiFile:${jarFile.toString()}")
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
        log("start hack")

        ClassPool classPool = ClassPool.getDefault()
        classPool.insertClassPath(jarFile.absolutePath)

        CtClass ctClass = classPool.get(TARGET_CLASS_NAME)
        if (ctClass.frozen) {
            //只需要修改一次 如果被修改过直接返回
            return ctClass.toBytecode()
        }

        List<String> codeList = new ArrayList<>()

        routeImplInfoMap.entrySet().each { Map.Entry<String, File> entry ->
            //    routeRoots.add(new com.licola.route.RouteApp.Route());
            classPool.insertClassPath(entry.getValue().absolutePath)
            codeList.add(" ${TARGET_INVOKE_METHOD_NAME}(new ${entry.getKey()}());")
        }

        CtMethod ctMethod = ctClass.getDeclaredMethod(TARGET_CLASS_METHOD_NAME)

        codeList.each { String code ->
            ctMethod.insertBefore(code)
        }

        def bytes = ctClass.toBytecode()

        return bytes
    }

    void readClassWithPath(File input, File dest) {
        def root = input.absolutePath
        input.eachFileRecurse { File file ->
            String filePath = file.absoluteFile
            if (!file.isFile()) return
            if (!filePath.endsWith(".class")) return
            def className = getClassName(root, filePath)
            InputStream inputStream = new FileInputStream(new File(filePath))
//            log("filePath:${filePath} className:${className}")
            findRouteImpl(inputStream, className, dest)
            inputStream.close()
        }
    }

    void readClassWithJar(File input, File dest) {
        JarFile jarFile = new JarFile(input)
        Enumeration<JarEntry> entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement()
            String entryName = entry.getName()
            if (!entryName.endsWith(".class")) continue
            String className = entryName.substring(0, entryName.length() - ".class".length()).replaceAll("/", ".")
//            log("entryName:${entryName} className:${className}")
            InputStream inputStream = jarFile.getInputStream(entry)

            findRouteImpl(inputStream, className, dest)

            findRouteTargetFile(entryName, dest)

            inputStream.close()
        }
        jarFile.close()
    }

    boolean findRouteTargetFile(String entryName, File targetFile) {
        if (TARGET_CLASS_PATH == entryName) {
            targetApiFile = targetFile
            return true
        }

        return false
    }

    boolean findRouteImpl(InputStream inputStream, String className, File targetFile) {
        ClassReader reader = new ClassReader(inputStream)
        ClassNode node = new ClassNode()
        reader.accept(node, 1)
        String[] interfaces = reader.getInterfaces()

        if (interfaces == null || interfaces.length == 0) {
            return false
        }

        for (String interfaceName : interfaces) {
            if (TARGET_INTERFACE_NAME == interfaceName) {
                routeImplInfoMap.put(className, targetFile)
                return true
            }
        }

        return false

    }

    static String getClassName(String root, String classPath) {
        return classPath.substring(root.length() + "/".length(), classPath.length() - ".class".length()).replaceAll("/", ".")
    }
}