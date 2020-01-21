//import InlineWebWorker.EventListenerType.MESSAGE
//import kotlinext.js.jsObject
//import org.w3c.dom.Worker
//import org.w3c.dom.WorkerOptions
//import org.w3c.dom.events.Event
//import org.w3c.dom.url.URL
//import org.w3c.files.Blob
//import kotlin.js.Date
//
//typealias Function = (self: InlineWebWorker) -> Unit
//
//class InlineWebWorker(task: Function) {
//    companion object {
//        fun create(task: Function) = InlineWebWorker(task)
//    }
//
//    enum class EventListenerType(val value: String) {
//        MESSAGE("message"), ERROR("error")
//    }
//
//    var onError
//        get() = worker.onerror
//        set(value) {
//            worker.onerror = value
//        }
//
//    var onMessage
//        get() = worker.onmessage
//        set(value) {
//            worker.onmessage = value
//        }
//
//    private var worker: Worker
//
//    init {
//        fun argument() {
//            task(this)
//        }
//        val blob = Blob(
//            arrayOf(::argument),
//            jsObject { type = "text/javascript" }
//        )
//        console.log("blob: ", arrayOf(::argument))
//        worker = Worker(
//            URL.createObjectURL(
//                blob
//            )
//        )
//
//    }
//
//    fun postMessage(message: Any?, vararg transfer: Any?) {
//        worker.postMessage(message, transfer)
//    }
//
//    fun terminate() {
//        worker.terminate()
//    }
//
//    fun addEventListener(type: EventListenerType, options: WorkerOptions? = null, listener: Worker.(Event) -> Unit) {
//        fun yo(it: Event) {
//            listener(worker, it)
//        }
//        worker.addEventListener(type.value, ::yo, options)
//    }
//
//    fun removeEventListener(type: EventListenerType, options: WorkerOptions? = null, listener: Worker.(Event) -> Unit) {
//        worker.removeEventListener(type.value, {
//            listener(worker, it)
//        }, options)
//    }
//
//    fun dispatchEvent(evt: Event) = worker.dispatchEvent(evt)
//}
//
//fun exampmfsdf() {
//    val worker = InlineWebWorker { self ->
//        val sleep = { delay: Int ->
//            val startTime = Date.now();
//            val endTime = Date.now() + delay;
//
//            while (true) {
//                if (endTime - Date.now() <= 0) {
//                    break
//                }
//            }
//        }
//        sleep(5000);
//
//        self.postMessage(MESSAGE, "halo from other thread", "boe")
//    }
//    worker.addEventListener(MESSAGE) { data ->
//        console.log(data)
//    }
////    worker.postMessage(null) // kickstart it so self.onmessage gets executed
//}