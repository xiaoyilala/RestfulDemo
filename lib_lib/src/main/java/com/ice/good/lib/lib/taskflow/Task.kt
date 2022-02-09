package com.ice.good.lib.lib.taskflow

import androidx.core.os.TraceCompat
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

abstract class Task @JvmOverloads constructor(
    /**任务名称*/
    val id:String,
    /**是否是异步任务*/
    val asyncTask:Boolean = false,
    /**延迟时间*/
    val delayMils:Long = 0,
    /**优先级*/
    var priority:Int = 0) :Runnable, Comparable<Task>{

    var executeTime:Long = -1
        protected set

    @TaskState
    var state:Int = TaskState.IDLE
        protected set

    /**该任务依赖的任务*/
    val dependTasks:MutableList<Task> = ArrayList()
    // 只有当dependTasks集合中的所有任务执行完，当前才可以执行

    /**依赖于该任务的任务*/
    val behindTasks:MutableList<Task> = ArrayList()
    //只有当该任务执行完，behindTasks集合中的后置任务才可以执行

    //用于运行时log 统计输出，输出当前task依赖了那些前置任务，这些前置任务的名称 我们将它存储在这里
    val dependTasksName:MutableList<String> = ArrayList()

    //任务运行状态监听器集合
    private val taskListeners:MutableList<TaskListener> = ArrayList()

    //用于输出 task 运行时的日志
    private var taskRuntimeListener: TaskRuntimeListener? = TaskRuntimeListener()

    fun addTaskListener(listener: TaskListener){
        if(!taskListeners.contains(listener)){
            taskListeners.add(listener)
        }
    }

    open fun start(){
        if(state!=TaskState.IDLE){
            throw RuntimeException("cannot run task $id again")
        }
        toStart()
        executeTime = System.currentTimeMillis()
        TaskRuntime.executeTask(this)
    }

    /**给当前task 添加一个 前置的依赖任务*/
    open fun dependOn(dependTask: Task){
        var task = dependTask
        if(task != this){
            if(dependTask is Project){
                task = dependTask.endTask
            }
            dependTasks.add(task)

            if(!task.behindTasks.contains(this)){
                task.behindTasks.add(this)
            }
        }
    }

    /**给当前task 移除一个前置依赖任务*/
    open fun removeDepend(dependTask: Task){
        var task = dependTask
        if(task != this){
            if(dependTask is Project){
                task = dependTask.endTask
            }
            dependTasks.remove(task)
            dependTasksName.add(task.id)

            if(task.behindTasks.contains(this)){
                task.behindTasks.remove(this)
            }
        }
    }

    /**给当前任务添加后置依赖项*/
    open fun behind(behindTask: Task){
        var task = behindTask
        if(task!=this){
            if(behindTask is Project){
                task = behindTask.startTask
            }
            behindTasks.add(task)

            task.dependOn(this)
        }
    }

    /**给当前任务移除后置依赖项*/
    open fun removeBehind(behindTask: Task) {
        var task = behindTask
        if (behindTask != this) {
            if (behindTask is Project) {
                task = behindTask.startTask
            }
            behindTasks.remove(task)

            task.removeDepend(this)
        }
    }

    override fun run() {
        TraceCompat.beginSection(id)

        toRunning()
        run(id)
        toFinish()

        notifyBehindTasks()

        recycle()

        TraceCompat.endSection()
    }

    private fun notifyBehindTasks(){
        //通知后置任务去尝试执行
        if(behindTasks.isNotEmpty()){
            if (behindTasks.size > 1) {
                Collections.sort(behindTasks, TaskRuntime.taskComparator)
            }

            for(behindTask in behindTasks){
                behindTask.dependTaskFinished(this)
            }
        }
    }

    private fun dependTaskFinished(dependTask: Task){
        if(dependTasks.isEmpty()){
            return
        }

        dependTasks.remove(dependTask)

        if (dependTasks.isEmpty()) {
            start()
        }
    }

    private fun toStart() {
        state = TaskState.START
        TaskRuntime.setTaskRuntimeInfoStateTime(this)
        for(listener in taskListeners){
            listener.onStart(this)
        }
        taskRuntimeListener?.onStart(this)
    }

    private fun toRunning() {
        state = TaskState.RUNNING
        TaskRuntime.setTaskRuntimeInfoStateTime(this)
        TaskRuntime.setTaskRuntimeInfoThreadName(this, Thread.currentThread().name)
        for(listener in taskListeners){
            listener.onRunning(this)
        }
        taskRuntimeListener?.onRunning(this)
    }

    private fun toFinish() {
        state = TaskState.FINISHED
        TaskRuntime.setTaskRuntimeInfoStateTime(this)
        TaskRuntime.removeBlockTask(this.id)
        // TODO: 2022/2/8
        for(listener in taskListeners){
            listener.onFinish(this)
        }
        taskRuntimeListener?.onFinish(this)
    }

    private fun recycle() {
        dependTasks.clear()
        behindTasks.clear()
        taskListeners.clear()
        taskRuntimeListener = null
    }

    override fun compareTo(other: Task): Int {
        return TaskUtil.compareTask(this, other)
    }

    abstract fun run(id: String)

}