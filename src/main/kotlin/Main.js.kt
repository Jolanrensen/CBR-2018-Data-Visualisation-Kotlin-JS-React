import data.Data
import data.Examenlocatie
import kotlinext.js.asJsObject
import kotlinext.js.jsObject
import libs.*
import libs.kworker.JobDescriptor
import libs.kworker.Jobs
import libs.kworker.JobsMainSync
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.workers.ServiceWorkerGlobalScope
import react.dom.render
import kotlin.browser.document

external fun alert(message: Any?)

object DemoXorJob : JobDescriptor {
    override suspend fun execute(args: Array<Any?>): Array<Any?> {
        val arg0 = args[0] as String
        val arg1 = args[1] as String


        val map = jsObjectOf<Examenlocatie>()
        map["dkfmslkdm"] = Examenlocatie(
            naam = arg0,
            straatnaam = arg0,
            huisnummer = arg0,
            huisnummerToevoeging = arg1,
            postcode = arg1,
            plaatsnaam = arg1,
            gemeente = arg1
        )
        map["qwerty"] = Examenlocatie(
            naam = arg0,
            straatnaam = arg0,
            huisnummer = arg0,
            huisnummerToevoeging = arg1,
            postcode = arg1,
            plaatsnaam = arg1,
            gemeente = arg1
        )

        return arrayOf(
            map
        )
    }
}


fun main() {
    println("Hello Kotlin/JS")


    // registering all workers + test
    JobsMainSync({
        register(DemoXorJob)
        register(Data.BuildAllDataJob)
    }, {
        val array: Array<Any?> = arrayOf("eerste", "tweede")

        println(array.toList())

//        val res = Jobs().execute(
//                DemoXorJob,
//                array
//            )[0] as JavaScriptObject<Examenlocatie>
//
//
//        console.log("examenlocatie: ", res)
    })

//
//    WorkerInterfaceImplBrowser.runEntry(EmptyCoroutineContext) {
//        WorkerFork({
//            while (true) {
//                val message = recv()
//                println("IN WORKER ${getWorkerId()} $message")
//                send(WorkerMessage("reply", "demo"))
//            }
//        }, {
//            val worker1 = Worker()
//            val worker2 = Worker()
//            println("Sending messages")
//            worker1.send(WorkerMessage("hello", "world"))
//            worker1.send(WorkerMessage("hello", "world"))
//            worker1.send(WorkerMessage("hello", "world"))
//
//            val workerId = getWorkerId()
//            println("IN MAIN $workerId ${worker1.recv()}")
//            println("IN MAIN $workerId ${worker1.recv()}")
//            println("IN MAIN $workerId ${worker1.recv()}")
//            worker2.send(WorkerMessage("hello", "world"))
//            worker2.send(WorkerMessage("hello", "world"))
//            println("IN MAIN $workerId ${worker2.recv()}")
//            println("IN MAIN $workerId ${worker2.recv()}")
//
//            // @TODO: Kotlin.JS BUG! getWorkerId() is not appended
//            //println("IN MAIN ${getWorkerId()} ${worker1.recv()}")
//            //println("IN MAIN ${getWorkerId()} ${worker1.recv()}")
//            //println("IN MAIN ${getWorkerId()} ${worker1.recv()}")
//            //worker2.send(WorkerMessage("hello", "world"))
//            //worker2.send(WorkerMessage("hello", "world"))
//            //println("IN MAIN ${getWorkerId()} ${worker2.recv()}")
//            //println("IN MAIN ${getWorkerId()} ${worker2.recv()}")
//        })
//    }


    // window.onload = {
    val root = document.getElementById("root")
    render(root) {
        app()
    }


    // Data.buildData()
//     val opleider = Data.alleOpleiders.values.find {
//         it.naam.contains("ANWB") && it.plaatsnaam == "BREDA"
//     }!!
    //
    // println("${
    // Data.getResults(listOf(opleider)).asSequence()
    //     .filter { it.product == Product.A || it.product == Product.A_NO }
    //     .sumBy {
    //         it.examenResultaatAantallen.asSequence()
    //             .voldoende
    //             .eersteExamen
    //             .sumBy { it.aantal }
    //     }
    // }/${
//     Data.getResults(listOf(opleider)).asSequence()
//         .filter { it.product == Product.A || it.product == Product.A_NO }
//         .sumBy {
//             it.examenResultaatAantallen.asSequence()
//                 .eersteExamen
//                 .automaat
//                 .sumBy { it.aantal }
//         }
    // }")
    // }
    // val xmlhttp = XMLHttpRequest()
    // xmlhttp.open("GET", "pc6hnr20180801_gwb-vs2.csv", false)
    //
    // xmlhttp.send()
    // val result = (if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null)!!
    //
    // val postcodeToGemeente = result
    //     .split('\n')
    //     .asSequence()
    //     .map { it.split(';') }
    //     .map { it.getOrElse(0) { "" } to it.getOrElse(4) { "" } }
    //     .toMap() // filters out doubles
    //     .map { (postcode, gemeente) -> "$postcode;$gemeente" }
    //     .joinToString(separator = "\n")
    // println(postcodeToGemeente)
}

fun Boolean.toInt() = if (this) 1 else 0

external val self: ServiceWorkerGlobalScope
fun runAsync(run: (evt: MessageEvent) -> Unit) {
    var worker: Worker? = null
    fun terminate() = worker!!.terminate()

    worker = Worker(
        URL.createObjectURL(
            Blob(
                arrayOf(
                    {
                        self.onmessage = {
                            run(it)
                            terminate()
                        }
                    }()
                ),
                jsObject { type = "text/javascript" }
            )
        )
    ).apply {
        postMessage(null)
    }
}





























