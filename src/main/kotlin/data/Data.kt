package data

import data.ExamenResultaat.*
import data.ExamenResultaatCategorie.*
import data.ExamenResultaatVersie.*
import io.data2viz.time.Date
import org.w3c.xhr.XMLHttpRequest

object Data {
    val alleResultaten = arrayListOf<Resultaat>()

    val alleOpleiders = hashMapOf<String, Opleider>()
    val alleExamenLocaties = hashMapOf<String, Examenlocatie>()

    init {
        val xmlhttp = XMLHttpRequest()
        xmlhttp.open("GET", "opleiderresultaten-01072017-tm-30062018.csv", false)

        xmlhttp.send()
        val result = if (xmlhttp.status == 200.toShort()) xmlhttp.responseText else null

        val csv = result
            ?.split('\n')
            ?.map { it.split(';') }

        if (csv == null) {
            println("Reading data failed!!")
        } else {
//            val headers = csv.first()
            val data = csv.drop(1)
            for ((i, line) in data.withIndex()) {
                alleResultaten.add(
                    Resultaat(
                        id = i,
                        opleider = alleOpleiders.getOrPut(line[0]) {
                            Opleider(
                                code = line[0],
                                naam = line[1],
                                startdatum = line[2].split('-').let {
                                    Date(
                                        day = it[0].toInt(),
                                        month = it[1].toInt(),
                                        year = it[2].toInt(),
                                        hour = 0,
                                        minute = 0,
                                        second = 0,
                                        millisecond = 0
                                    )
                                },
                                einddatum = line[3].split('-').let {
                                    Date(
                                        day = it[0].toInt(),
                                        month = it[1].toInt(),
                                        year = it[2].toInt(),
                                        hour = 0,
                                        minute = 0,
                                        second = 0,
                                        millisecond = 0
                                    )
                                },
                                straatnaam = line[4],
                                huisnummer = line[5],
                                huisnummerToevoeging = line[6],
                                postcode = line[7],
                                plaatsnaam = line[8]
                            )
                        },
                        product = Product.valueOf(
                            line[11].replace('-', '_')
                        ),
                        examenlocatie = alleExamenLocaties.getOrPut(line[13]) {
                            Examenlocatie(
                                naam = line[13],
                                straatnaam = line[14],
                                huisnummer = line[15],
                                huisnummerToevoeging = line[16],
                                postcode = line[17],
                                plaatsnaam = line[18]
                            )
                        },
                        examenResultaatAantallen = listOf(
                            ExamenResultaatAantal(
                                examenResultaatVersie = EersteExamenOfToets,
                                examenResultaatCategorie = Automaat,
                                examenResultaat = Voldoende,
                                aantal = line[23].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EersteExamenOfToets,
                                examenResultaatCategorie = Automaat,
                                examenResultaat = OnVoldoende,
                                aantal = line[24].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EersteExamenOfToets,
                                examenResultaatCategorie = Combi,
                                examenResultaat = Voldoende,
                                aantal = line[25].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EersteExamenOfToets,
                                examenResultaatCategorie = Combi,
                                examenResultaat = OnVoldoende,
                                aantal = line[26].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EersteExamenOfToets,
                                examenResultaatCategorie = Handgeschakeld,
                                examenResultaat = Voldoende,
                                aantal = line[27].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = EersteExamenOfToets,
                                examenResultaatCategorie = Handgeschakeld,
                                examenResultaat = OnVoldoende,
                                aantal = line[28].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HerexamenOfToets,
                                examenResultaatCategorie = Automaat,
                                examenResultaat = Voldoende,
                                aantal = line[31].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HerexamenOfToets,
                                examenResultaatCategorie = Automaat,
                                examenResultaat = OnVoldoende,
                                aantal = line[32].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HerexamenOfToets,
                                examenResultaatCategorie = Combi,
                                examenResultaat = Voldoende,
                                aantal = line[33].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HerexamenOfToets,
                                examenResultaatCategorie = Combi,
                                examenResultaat = OnVoldoende,
                                aantal = line[34].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HerexamenOfToets,
                                examenResultaatCategorie = Handgeschakeld,
                                examenResultaat = Voldoende,
                                aantal = line[35].toInt()
                            ),
                            ExamenResultaatAantal(
                                examenResultaatVersie = HerexamenOfToets,
                                examenResultaatCategorie = Handgeschakeld,
                                examenResultaat = OnVoldoende,
                                aantal = line[36].toInt()
                            )
                        )
                    )
                )
            }
        }
    }

}