package com.ice.good.lib.lib.taskflow

import android.text.TextUtils
import android.util.Log
import com.ice.good.lib.common.MainHandler
import com.ice.good.lib.lib.BuildConfig
import com.ice.good.lib.lib.executor.GoodExecutor
import java.lang.StringBuilder
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * taskFlow 运行时的任务调度器，会初始化所有任务，并提升阻塞任务的优先级
 *
 * 1. 根据task的属性以不同的策略（线程，同步，延迟）调度任务
 * 2. 校验依赖树中是否存在环形依赖
 * 3. 校验依赖树中是否存在taskId相同的任务
 * 4. 统计所有 task的运行时信息（线程，状态，开始执行时间，耗时时间，是否是阻塞任务），用于log输出
 */
internal object TaskRuntime {

    //只有当blockTasksId当中的任务都执行完了才会释放application的阻塞，才会拉起launchActivity
    private val blockTaskIds:MutableList<String> = mutableListOf()

    //在主线程中执行的非阻塞任务
    private val waitingTasks:MutableList<Task> = mutableListOf()

    private val taskRuntimeInfos:MutableMap<String, TaskRuntimeInfo> = HashMap()

    val taskComparator = Comparator<Task> { task1, task2 -> TaskUtil.compareTask(task1, task2) }

    /**根据 task 的属性以不同的策略 调度 task*/
    @JvmStatic
    fun executeTask(task: Task){
        if(task.asyncTask){
            GoodExecutor.execute(runnable = task)
        }else{
            //需要在主线程执行的
            if(task.delayMils>0 && !hasBlockBehindTask(task)){
                //延迟任务 且没有后置的阻塞任务
                MainHandler.postDelay(task.delayMils, task)
                return
            }

            if(!hasBlockTasks() || getTaskRuntimeInfo(task.id)?.blockTask == true){
                //没有了阻塞任务 或者 自己是阻塞任务
                task.run()
            }else{
                addWaitingTask(task)
            }
        }
    }

    /**
     * 检查：
     * 1、是否存在环形依赖；
     * 2、是否存在相同id（名称）的任务
     *
     * 初始化taskRuntimeInfo
     * */
    @JvmStatic
    fun checkAndInit(task: Task){
        val visitor = linkedSetOf<Task>()
        visitor.add(task)
        innerCheckAndInit(task, visitor)

        //提升 阻塞任务task 前置依赖任务的优先级
        val iterator = blockTaskIds.iterator()
        while (iterator.hasNext()){
            val taskId = iterator.next()
            //检查这个阻塞任务 是否存在依赖树中
            if(!taskRuntimeInfos.containsKey(taskId)){
                throw java.lang.RuntimeException("block task ${task.id} not in dependency tree.")
            }else{
                resetPriority(getTaskRuntimeInfo(taskId)?.task)
            }
        }
    }

    @JvmStatic
    fun getTaskRuntimeInfo(id: String):TaskRuntimeInfo?{
        return taskRuntimeInfos[id]
    }

    @JvmStatic
    fun addBlockTask(id : String){
        if(!TextUtils.isEmpty(id)){
            blockTaskIds.add(id)
        }
    }

    @JvmStatic
    fun addBlockTasks(vararg ids:String){
        if(ids.isNotEmpty()){
            for(id in ids){
                addBlockTask(id)
            }
        }
    }

    @JvmStatic
    fun removeBlockTask(id: String) {
        blockTaskIds.remove(id)
    }

    @JvmStatic
    fun hasBlockTasks(): Boolean {
        return blockTaskIds.iterator().hasNext()
    }

    @JvmStatic
    fun hasWaitingTasks(): Boolean {
        return waitingTasks.iterator().hasNext()
    }

    @JvmStatic
    fun setTaskRuntimeInfoThreadName(task: Task, threadName:String?){
        getTaskRuntimeInfo(task.id)?.threadName = threadName
    }

    @JvmStatic
    fun setTaskRuntimeInfoStateTime(task: Task){
        getTaskRuntimeInfo(task.id)?.setStateTime(task.state, System.currentTimeMillis())
    }

    /**把一个主线程上需要执行的任务，但又不影响launchActivity的启动，添加到等待队列*/
    private fun addWaitingTask(task: Task) {
        if (!waitingTasks.contains(task)) {
            waitingTasks.add(task)
        }
    }

    /**判断任务是否存在后置的阻塞任务*/
    private fun hasBlockBehindTask(task: Task):Boolean{
        if(task is Project.CriticalTask){
            return false
        }
        val behindTasks = task.behindTasks
        for (behindTask in behindTasks) {
            //需要判断一个task 是不是阻塞任务 ，blockTaskIds
            val behindTaskInfo = getTaskRuntimeInfo(behindTask.id)
            return if (behindTaskInfo != null && behindTaskInfo.blockTask) {
                true
            } else {
                hasBlockBehindTask(behindTask)
            }
        }
        return false
    }

    @JvmStatic
    fun runWaitingTasks(){
        if(hasWaitingTasks()){
            if(waitingTasks.size>1){
                Collections.sort(waitingTasks, taskComparator)
            }
            if(hasBlockTasks()){
                val headWaitingTask = waitingTasks.removeAt(0)
                headWaitingTask.run()
            }else{
                for (waitingTask in waitingTasks) {
                    MainHandler.postDelay(waitingTask.delayMils, waitingTask)
                }
                waitingTasks.clear()
            }
        }
    }

    private fun innerCheckAndInit(task: Task, visitor: LinkedHashSet<Task>) {
        //初始化taskRuntimeInfo 并校验是否存在相同的任务名称 task.ID
        var taskRuntimeInfo = getTaskRuntimeInfo(task.id)
        if(taskRuntimeInfo == null){
            taskRuntimeInfo = TaskRuntimeInfo(task)
            if(blockTaskIds.contains(task.id)){
                taskRuntimeInfo.blockTask = true
            }
            taskRuntimeInfos[task.id] = taskRuntimeInfo
        }else{
            if (!taskRuntimeInfo.isSameTask(task)) {
                throw RuntimeException("not allow to contain the same id ${task.id}")
            }
        }

        //环形依赖校验1
        for(behindTask in task.behindTasks){
            if(!visitor.contains(behindTask)){
                visitor.add(behindTask)
            }else{
                throw  RuntimeException("not allow loopback dependency ,task id =${task.id}")
            }

            if(BuildConfig.DEBUG && behindTask.behindTasks.isEmpty()){
                val iterator = visitor.iterator()
                val sb = StringBuilder()
                while (iterator.hasNext()){
                    sb.append(iterator.next().id)
                    sb.append(" --> ")
                }
                val log = sb.toString()
                Log.i(TaskRuntimeListener.TAG, log.substring(0, log.length - 5))//减5是为了去除最后一个 -->
            }

            //环形依赖校验2
            innerCheckAndInit(behindTask, visitor)
            visitor.remove(behindTask)
        }
    }

    private fun resetPriority(task: Task?) {
        if (task == null) return
        task.priority = Int.MAX_VALUE
        for (dependTask in task.dependTasks) {
            resetPriority(dependTask)
        }
    }

}