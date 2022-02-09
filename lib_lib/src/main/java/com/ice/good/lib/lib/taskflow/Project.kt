package com.ice.good.lib.lib.taskflow

class Project private constructor(id:String):Task(id) {

    lateinit var startTask:Task
    lateinit var endTask:Task

    override fun start() {
        startTask.start()
    }

    override fun dependOn(dependTask: Task) {
        startTask.dependOn(dependTask)
    }

    override fun removeDepend(dependTask: Task) {
        startTask.removeDepend(dependTask)
    }

    override fun behind(behindTask: Task) {
        endTask.behind(behindTask)
    }

    override fun removeBehind(behindTask: Task) {
        endTask.removeBehind(behindTask)
    }

    class Builder(id: String, iTaskCreator: ITaskCreator){
        private val mTaskFactory = TaskFactory(iTaskCreator)
        private val mStartTask: Task = CriticalTask(id + "_start")
        private val mEndTask: Task = CriticalTask(id + "_end")
        private val mProject: Project = Project(id)
        private var mPriority = 0//默认为该任务组中 所有任务优先级的 最高的

        //本次添加进来的这个task 是否把start 节点当做前置依赖
        //那如果这个task 它存在与其他task的依赖关系，那么就不能直接添加到start 节点的后面了
        //而需要通过dependOn来指定任务的依赖关系
        private var mCurrentTaskShouldDependOnStartTask = false

        private var mCurrentAddTask: Task? = null

        fun add(id: String): Builder {
            val task = mTaskFactory.getTask(id)
            if (task.priority > mPriority) {
                mPriority = task.priority
            }
            return add(task)
        }

        private fun add(task: Task):Builder{
            if (mCurrentTaskShouldDependOnStartTask && mCurrentAddTask != null) {
                mStartTask.behind(mCurrentAddTask!!)
            }

            mCurrentAddTask = task
            mCurrentTaskShouldDependOnStartTask = true
            mCurrentAddTask!!.behind(mEndTask)

            return this
        }

        fun dependOn(id: String): Builder {
            return dependOn(mTaskFactory.getTask(id))
        }

        private fun dependOn(task: Task):Builder{
            task.behind(mCurrentAddTask!!)
            mEndTask.removeDepend(task)
            mCurrentTaskShouldDependOnStartTask = false
            return this
        }

        fun build():Project{
            if(mCurrentAddTask==null){
                mStartTask.behind(mEndTask)
            }else{
                if(mCurrentTaskShouldDependOnStartTask){
                    mStartTask.behind(mCurrentAddTask!!)
                }
            }
            mStartTask.priority = mPriority
            mEndTask.priority = mPriority
            mProject.startTask = mStartTask
            mProject.endTask = mEndTask

            return mProject
        }
    }

    private class TaskFactory(private val iTaskCreator: ITaskCreator){
        // 利用iTaskCreator创建task 实例，并管理
        private val mCacheTasks: MutableMap<String, Task> = HashMap()
        fun getTask(id:String):Task{
            var task = mCacheTasks[id]
            if(task!=null){
                return task
            }
            task = iTaskCreator.createTask(id)
            mCacheTasks[id] = task
            return task
        }
    }

    internal class CriticalTask internal constructor(id:String):Task(id){
        override fun run(id: String) {
            //nothing to do
        }
    }

    override fun run(id: String) {
        //不需要处理的
    }
}