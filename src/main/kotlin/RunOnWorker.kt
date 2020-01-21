//import org.w3c.dom.Worker
//import org.w3c.dom.url.URL
//import org.w3c.files.Blob
//import org.w3c.workers.ServiceWorkerGlobalScope
//import kotlin.browser.window
//
//
//interface Job<Arguments, Results> {
//    fun Arguments.execute(): Results
//}
//
//external val self: ServiceWorkerGlobalScope
//operator fun <Arguments, Results, J : Job<Arguments, Results>> J.invoke(
//    arguments: Arguments,
//    callback: (Results) -> Unit
//) {
//    val worker = Worker(URL.createObjectURL(
//        Blob(
//        arrayOf(
//            {
//                arguments.execute()
//            }()
//        )
//    )
//    ))
//
//    worker.onmessage = {
//        callback(it.data as Results)
//    }
//    worker.postMessage(arguments)
//
//
//
//
//}
//
/////// example
//
//interface MyArgs {
//    val test: String
//}
//
//interface MyResults {
//    val test2: String
//}
//
//object MyJob : Job<MyArgs, MyResults> {
//    override fun MyArgs.execute(): MyResults {
//        val doSomething = test + "yooo"
//
//        return object : MyResults {
//            override val test2 = doSomething
//        }
//    }
//}
//
//fun example() {
//
//    MyJob(object : MyArgs {
//        override val test = "boe "
//    }) {
//        println(it.test2)
//    }
//
//}