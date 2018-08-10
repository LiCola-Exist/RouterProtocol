import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

import java.util.function.BiConsumer
import java.util.function.Consumer

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
        System.out.println("------------------transform----------------------")
        System.out.println(transformInvocation.context.path)



    }




}