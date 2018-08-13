package com.licola.route.register

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

public class MyInjects {

    private static final ClassPool pool = ClassPool.getDefault()

    public static void inject(String path, Project project) {
        pool.appendClassPath(path)
        pool.appendClassPath(project.android.bootClasspath[0].toString())

        pool.importPackage("android.os.Bundle")

        File dir = new File(path)

        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (file.getName() == "MainActivity.class") {
                    CtClass ctClass = pool.getCtClass("com.licola.model.routerprotocol.MainActivity")
                    if (ctClass.isFrozen()) {
                        ctClass.defrost()
                    }

                    CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate")

                    String inserLine = """android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show(); """

                    ctMethod.insertBefore(inserLine)
                    ctClass.writeFile(path)
                    ctClass.detach()
                }
            }
        }
    }
}