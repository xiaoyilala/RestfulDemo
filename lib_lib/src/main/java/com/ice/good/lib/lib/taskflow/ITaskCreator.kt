package com.ice.good.lib.lib.taskflow

interface ITaskCreator {
    fun createTask(taskName: String): Task
}