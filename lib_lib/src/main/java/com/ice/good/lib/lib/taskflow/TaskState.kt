package com.ice.good.lib.lib.taskflow

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(TaskState.IDLE, TaskState.START, TaskState.RUNNING, TaskState.FINISHED)
annotation class TaskState{
    companion object{
        const val IDLE = 0//还未开始运行 初始状态
        const val START = 1//启动，可能需要调度
        const val RUNNING = 2//运行中
        const val FINISHED = 3//结束
    }
}
