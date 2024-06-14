package dev.nikdekur.ndkore.scheduler


interface Scheduler {

    /**
     * Starts a task in the scheduler.
     *
     * @param task The task to be executed.
     * @return The task object.
     */
    fun runTask(task: suspend () -> Unit): SchedulerTask

    /**
     * Starts a task with a specified interval.
     *
     * @param delay Delay before the first task launch (in milliseconds).
     * @param interval Interval between further task launches (in milliseconds).
     * @param task The task to be executed.
     * @return The task object.
     */
    fun runTaskTimer(delay: Long, interval: Long, task: suspend () -> Unit): SchedulerTask

    /**
     * Starts a task with a specified interval.
     *
     * @param interval Interval between further task launches (in milliseconds).
     * @param task The task to be executed.
     * @return The task object.
     */
    fun runTaskTimer(interval: Long, task: suspend () -> Unit) = runTaskTimer(interval, interval, task)

    /**
     * Starts the execution of a task after a specified time has elapsed.
     *
     * @param delay Delay before the task execution.
     * @param task The task to be executed.
     * @return The task object.
     */
    fun runTaskLater(delay: Long, task: suspend () -> Unit): SchedulerTask

    /**
     * Gets the task by its Id.
     *
     * @param id The task Id.
     * @return The task object or null if the task is not found.
     */
    fun getTask(id: Int): SchedulerTask?

    /**
     * Cancels all running tasks.
     */
    fun cancelAllTasks()
}