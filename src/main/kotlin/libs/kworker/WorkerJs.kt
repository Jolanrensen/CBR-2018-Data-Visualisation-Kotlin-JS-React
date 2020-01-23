package libs.kworker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

open class BaseJsWorkerInterface : WorkerInterface() {
    override fun runEntry(context: CoroutineContext, callback: suspend () -> Unit) {
        CoroutineScope(context).launch { callback() }
    }

    override fun suspendTest(callback: suspend () -> Unit): dynamic {
        return kotlin.js.Promise<dynamic> { resolve, reject ->
            callback.startCoroutine(object : Continuation<Unit> {
                override val context: CoroutineContext = EmptyCoroutineContext
                override fun resumeWith(result: Result<Unit>) {
                    val exception = result.exceptionOrNull()
                    if (exception != null) {
                        reject(exception)
                    } else {
                        resolve(Unit)
                    }
                }
            })
        }
    }
}

//val WorkerInterfaceImpl: WorkerInterface by lazy {
//    when {
////        com.soywiz.kworker.internal.ENVIRONMENT_IS_NODE -> WorkerInterfaceImplNode
//        com.soywiz.kworker.internal.ENVIRONMENT_IS_WEB_OR_WORKER -> WorkerInterfaceImplBrowser
//        else -> error("Unsupported javascript engine (not detected either Node, or Browser)")
//    }
//}
