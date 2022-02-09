package com.ice.good.lib.lib.taskflow

interface TaskListener {
    fun onStart(task: Task)

    fun onRunning(task: Task)

    fun onFinish(task: Task)
}