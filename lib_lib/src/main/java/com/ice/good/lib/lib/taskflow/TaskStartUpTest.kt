package com.ice.good.lib.lib.taskflow

import android.util.Log

object TaskStartUpTest {
    const val TASK_BLOCK_1 = "block_task_1"
    const val TASK_BLOCK_2 = "block_task_2"
    const val TASK_BLOCK_3 = "block_task_3"
    const val TASK_BLOCK_4 = "block_task_4"

    const val TASK_ASYNC_1 = "async_task_1"
    const val TASK_ASYNC_2 = "async_task_2"
    const val TASK_ASYNC_3 = "async_task_3"

    @JvmStatic
    fun start() {
        //apt annotationprocesstool 编译时去收集 项目中所有的task信息，
        /**
         * @TaskStragtegy(isAysnc=true,taskId='InitPushTask',delayMills=0)
         * class InitPushTask extends Task{
         *
         *    void run(String name){
         *
         *    }
         * }
         *
         * javapoet ---生成 TaskStartUp类
         */


        Log.e("TaskStartUp", "start")
        val project = Project.Builder("TaskStartUp", createTaskCreator())
            .add(TASK_BLOCK_1)
            .add(TASK_BLOCK_2).dependOn(TASK_ASYNC_3)
            .add(TASK_BLOCK_3)
            .add(TASK_ASYNC_1).dependOn(TASK_BLOCK_1)
            .add(TASK_ASYNC_2).dependOn(TASK_BLOCK_2).dependOn(TASK_ASYNC_3)
            .add(TASK_ASYNC_3)
            .build()

        val createTask4 = createTask(TASK_BLOCK_4, false)
        project.dependOn(createTask4)

        TaskFlowManager
//            .addBlockTask(TASK_BLOCK_1)
//            .addBlockTask(TASK_BLOCK_2)
//            .addBlockTask(TASK_BLOCK_3)
            .start(project)

        Log.e("TaskStartUp", "end")
    }

    private fun createTaskCreator(): ITaskCreator {
        return object : ITaskCreator {
            override fun createTask(taskName: String): Task {
                when (taskName) {
                    TASK_ASYNC_1 -> return createTask(taskName, true)
                    TASK_ASYNC_2 -> return createTask(taskName, true)
                    TASK_ASYNC_3 -> {
                        val createTask = createTask(taskName, true)
                        createTask.priority = 100
                        return createTask
                    }

                    TASK_BLOCK_1 -> return createTask(taskName, false)
                    TASK_BLOCK_2 -> return createTask(taskName, false)
                    TASK_BLOCK_3 ->{
                        val createTask = createTask(taskName, false)
                        createTask.priority = 100
                        return createTask
                    }
                }
                return createTask("default", false)
            }
        }
    }

    fun createTask(taskName: String, isAsync: Boolean): Task {
        return object : Task(taskName, isAsync) {
            override fun run(name: String) {
                Thread.sleep(if (isAsync) 2000 else 1000)
                Log.e("TaskStartUp", "task $taskName, $isAsync,finished")
            }
        }
    }
}