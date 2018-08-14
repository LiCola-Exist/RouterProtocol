package com.licola.route.register

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.MethodVisitor

public class TestTransform extends Transform {

    private Project mProject;

    TestTransform(Project mProject) {
        this.mProject = mProject
    }

    /**
     * transform的名称
     * @return
     */
    @Override
    String getName() {
        return "TestTransform"
    }

    /**
     * 需要处理的数据类型
     *
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指定要操作的内容范围
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 指明当前Transform是否支持 增量编译
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        System.out.println("------------------transform开始----------------------")
//        System.out.println(transformInvocation.context.path)

        transformInvocation.inputs.each { TransformInput input ->

            /**
             * 对目录组织（本地源码编译后的.class） input进行处理
             */
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //对源码进行处理
                MyInjects.inject(directoryInput.file.absolutePath, mProject)

                //获取output目录
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                )

                //将input复制到output指定目录中
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            /**
             * 对jar组织（外部依赖） input进行处理
             */
            input.jarInputs.each { JarInput jarInput ->

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        System.out.println("------------------transform结束----------------------")

    }


}