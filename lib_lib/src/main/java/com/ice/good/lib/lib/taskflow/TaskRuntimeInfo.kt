package com.ice.good.lib.lib.taskflow

import android.util.SparseArray

class TaskRuntimeInfo(val task: Task) {
    val stateTime = SparseArray<Long>()
    var blockTask = false
    var threadName:String? = null

    fun setStateTime(@TaskState state: Int, timeMils:Long){
        stateTime.put(state, timeMils)
    }

    fun isSameTask(task: Task?):Boolean{
        return task!=null && this.task == task
    }

    override fun toString(): String {
        return "TaskRuntimeInfo(task=$task, stateTime=$stateTime, blockTask=$blockTask, threadName=$threadName)"
    }

}