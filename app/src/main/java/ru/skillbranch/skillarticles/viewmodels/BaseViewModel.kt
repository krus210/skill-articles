package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.*

abstract class BaseViewModel<T>(initState: T) : ViewModel() {

    val notifications = MutableLiveData<Event<Notify>>()

    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    //not null current state
    protected val currentState
        get() = state.value!!

    /***
     * лямбда выражение принимает в качестве аргумента текущее состояние и возвращает
     * модифицированное состояние, которое присваивается текущему состоянию
     */
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updateState: T = update(currentState)
        state.value = updateState
    }

    protected fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    /***
     * более компактная форма записи observe() метода LiveData принимает последним аргумент лямбда
     * выражение обрабатывающее изменение текущего стостояния
     */
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    /***
    *более компактная форма записи observe вызывает лямбда-выражение-обработчик только в том случае
    *если сообщение не было уже обработанно, реализует данное поведение благодаря EventObserver
    */
    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver {
            onNotify(it)})
    }

    /***
     *функция принимает источник данных и лямбда выражение, обрабатывающее поступающие данные
     *лямбда принимает новые данные и текущее состояние, изменяет его и возвращает
     *модифицированное состояние устанавливается как текущее
     */
    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

}

class ViewModelFactory(private val params: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false

    //возвращает контент, который еще не был обработан, иначе null
    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): E = content
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {
    //в качестве аргумента принимает лямбда выражение-обработчик, в которую передается необработанное
    //ранее событие, получаемое в реализации метода Observer'a onChanged
    override fun onChanged(event: Event<E>?) {
        //если есть необработанное событие (контент), передаем в качестве аргумента в лямбду
        //onEventUnhandledContent
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }

}

sealed class Notify(val message: String) {
    data class TextMessage(val msg: String) : Notify(msg)

    data class ActionMessage(
        val msg: String,
        val actionLAbel: String,
        val actionHandler: (() -> Unit)?
    ): Notify(msg)

    data class ErrorMessage(
        val msg: String,
        val errorLAbel: String,
        val errorHandler: (() -> Unit)?
    ): Notify(msg)
}