package data

import io.data2viz.time.Date

// Opleidercode;Opleidernaam;Opleider Startdatum;Opleider Einddatum;Opleider Straatnaam ;Opleider Huisnummer;Opleider Huisnummer toevoeging;Opleider Postcode;Opleider Plaatsnaam;Categoriecode;Categorienaam;Productcode;Productnaam;Examenlocatienaam;Examenlocatie Straatnaam;Examenlocatie Huisnummer;Examenlocatie Huisnummer toevoeging;Examenlocatie Postcode;Examenlocatie Plaatsnaam;Examenperiode Begindatum;Examenperiode Einddatum;Totaal Eerste Examens/Toetsen Voldoende;Totaal Eerste Examens/Toetsen Onvoldoende;Eerste Examens/Toetsen Automaat Voldoende;Eerste Examens/Toetsen Automaat Onvoldoende;Eerste Examens/Toetsen Combi Voldoende;Eerste Examens/Toetsen Combi Onvoldoende;Eerste Examens/Toetsen Handgeschakeld Voldoende;Eerste Examens/Toetsen Handgeschakeld Onvoldoende;Totaal Herexamens/Toetsen Voldoende;Totaal Herexamens/Toetsen Onvoldoende;Herexamens/Toetsen Automaat Voldoende;Herexamens/Toetsen Automaat Onvoldoende;Herexamens/Toetsen Combi Voldoende;Herexamens/Toetsen Combi Onvoldoende;Herexamens/Toetsen Handgeschakeld Voldoende;Herexamens/Toetsen Handgeschakeld Onvoldoende
data class Result(
    val id: Long,
    val opleider: Opleider




) {
}