package dev.nikdekur.ndkore.module

import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.map.multi.MultiHashMap
import dev.nikdekur.ndkore.module.Module.Companion.id
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class ModulesManager<A> {

    val logger: Logger = LoggerFactory.getLogger("Classlint-ModulesManager")

    val modules = LinkedHashMap<String, Module<A>>()
    val modulesState = HashMap<String, ModuleState>()

    var isLoading = false
    var isUnloading = false


    fun addModule(module: Module<A>) {
        modules.addById(module)
    }




    fun getModule(moduleName: String): Module<A>? {
        return modules[moduleName]
    }

    fun <T : Module<A>> getModule(moduleClass: Class<T>): T {
        val id = moduleClass.id
        val module = getModule(id) ?: throw ModuleNotFoundException(id)
        if (!moduleClass.isInstance(module))
            throw IncorrectModuleTypeException(id, moduleClass.name, module.javaClass.name)
        @Suppress("UNCHECKED_CAST")
        return module as T
    }

    inline fun <reified T : Module<A>> getModule(): T {
        return getModule(T::class.java)
    }






    fun unregister(moduleName: String) {
        val module = getModule(moduleName)
        if (module != null) {
            unregister(module)
        }
    }

    fun unregister(module: Module<A>) {
        modules.remove(module.id)
    }


    fun loadAll() {
        isLoading = true
        val sorted = sortModules()
        logger.info("Load order: ${sorted.joinToString { it.id }}")
        sorted.forEach {
            updateState(it.id, ModuleState.LOADING)
            try {
                it.onLoad()
            } catch (e: Exception) {
                logger.error("Error while loading module '${it.id}'!", e)
            }
            updateState(it.id, ModuleState.LOADED)
        }
        isLoading = false
    }

    fun unloadAll() {
        isUnloading = true
        // Unload in reverse order, because of dependencies
        sortModules().reversed().forEach {
            updateState(it.id, ModuleState.UNLOADING)
            try {
                it.onUnload()
            } catch (e: Exception) {
                logger.error("Error while unloading module '${it.id}'!", e)
            }
            updateState(it.id, ModuleState.UNLOADED)
        }
        isUnloading = false
    }




    val tasks = MultiHashMap<TaskMoment, String, MutableList<ModuleTask<A, *>>>()
    fun <T : Module<A>> addTask(moduleClass: Class<T>, moment: TaskMoment, taskId: String, task: (T) -> Unit) {

        val module = getModule(moduleClass)
        if (module.javaClass != moduleClass) {
            logger.warn("Error while adding task '$taskId': module '${moduleClass.name}' is not instance of '${moduleClass.name}'!")
            return
        }

        val moduleId = module.id

        val momentTasks = tasks.computeIfAbsent(moment, moduleId) { ArrayList() }
        val moduleTask = ModuleTask(moment, taskId, moduleClass, task)
        momentTasks.add(moduleTask)
        if (moment == TaskMoment.AFTER_LOAD && isLoaded(moduleId)) {
            executeTask(moduleTask)
        }
    }

    inline fun <reified T : Module<A>> addTask(moment: TaskMoment, taskId: String, noinline task: (T) -> Unit) {
        addTask(T::class.java, moment, taskId, task)
    }

    inline fun <reified T : Module<A>> addAfterLoadTask(taskId: String, noinline task: (T) -> Unit) {
        addTask(T::class.java, TaskMoment.AFTER_LOAD, taskId, task)
    }

    private fun executeTasks(moment: TaskMoment, moduleId: String) {
        val tasks = tasks[moment, moduleId]
        tasks?.forEach { executeTask(it) }
    }

    private fun <T : Module<A>> executeTask(task: ModuleTask<A, T>) {
        val clazz = task.moduleClass
        try {
            val module = getModule(task.moduleClass)
            task.task(module)
        } catch (e: IllegalArgumentException) {
            logger
                .warn("Module '${clazz.id}' was existing before adding task '${task.id}', but now occurs error!", e)
        } catch (e: Exception) {
            logger
                .warn("Uncaught exception while executing task '${task.id}'!", e)
        }
    }





    fun getState(moduleName: String): ModuleState {
        return modulesState[moduleName] ?: ModuleState.UNLOADED
    }

    fun isLoaded(moduleName: String) = getState(moduleName) == ModuleState.LOADED
    fun isLoading(moduleName: String) = getState(moduleName) == ModuleState.LOADING
    fun isUnloading(moduleName: String) = getState(moduleName) == ModuleState.UNLOADING
    fun isUnloaded(moduleName: String) = getState(moduleName) == ModuleState.UNLOADED



    private fun updateState(moduleId: String, state: ModuleState) {
        val moment = state.toMoment()

        if (moment.isBefore)
            executeTasks(moment, moduleId)

        modulesState[moduleId] = state

        if (moment.isAfter)
            executeTasks(state.toMoment(), moduleId)
    }


    fun reloadAll() {
        unloadAll()
        loadAll()
    }


    private fun sortModules(): Collection<Module<A>> {
        val sortedModules = LinkedHashSet<Module<A>>()
        val addedModules = HashSet<String>()

        fun addModule(module: Module<A>) {
            if (sortedModules.contains(module)) return
            // Check for self-dependency
            val dependencies = module.dependencies
            if (dependencies.before.contains(module.javaClass) || dependencies.after.contains(module.javaClass)) {
                throw SelfDependencyException(module.id)
            }

            // Check for recursive dependency
            if (dependencies.before.any { beforeModule ->
                val id = beforeModule.id
                addedModules.contains(id) && id == module.id
            }) throw RecursiveDependencyException(module.id)

            // Load after those modules
            val afterModules = dependencies.after

            // Load before those modules
            val beforeModules = dependencies.before

            afterModules.forEach { afterModule ->
                modules[afterModule.id]?.let(::addModule)
            }

            sortedModules.add(module)
            addedModules.add(module.id)

            beforeModules.forEach { beforeModule ->
                modules[beforeModule.id]?.let(::addModule)
            }
        }

        modules.values.filter {
            val dependencies = it.dependencies
            dependencies.first || (dependencies.after.isEmpty() && dependencies.before.isEmpty()  )
        }.forEach(::addModule)

        modules.values.forEach(::addModule)

        modules.values.filter { it.dependencies.last }.forEach(::addModule)

        return sortedModules
    }

}
