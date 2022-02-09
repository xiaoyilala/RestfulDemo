package com.ice.good.lib.lib.taskflow

object TaskUtil {
    fun compareTask(task1: Task, task2: Task):Int{
        return task2.priority - task1.priority
    }
}