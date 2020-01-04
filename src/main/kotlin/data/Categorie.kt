package data

enum class Categorie(val omschrijving: String) {
    A("Praktijkexamen motor verkeersdeelneming A rechtstreeks"),
    A1("Praktijkexamen motor verkeersdeelneming A1"),
    A1_T("Tussentijdse toets motor verkeersdeelneming A1"),
    A2("Praktijkexamen motor verkeersdeelneming A2"),
    A2_G("Praktijkexamen motor verkeersdeelneming A2 getrapt"),
    A2_T("Tussentijdse toets motor verkeersdeelneming A2"),
    ADR1("ADR klasse 1 (ADR1)"),
    ADR1V("ADR klasse 1 verlenging (ADR1V)"),
    ADR7("ADR klasse 7 (ADR7)"),
    ADR7V("ADR klasse 7 verlenging (ADR7V)"),
    ADRB("ADR basis (ADRB)"),
    ADRBV("ADR basis verlenging (ADRBV)"),
    ADRT("ADR tank (ADRT)"),
    ADRTV("ADR tank verlenging (ADRTV)"),
    AM("Praktijkexamen bromfiets"),
    AMTH("Theorie-examen bromfiets"),
    ATH("Theorie-examen motor"),
    AVB_A("Praktijkexamen motor voertuigbeheersing A"),
    AVB_A1("Praktijkexamen motor voertuigbeheersing A1"),
    AVB_A2("Praktijkexamen motor voertuigbeheersing A2"),
    A_G("Praktijkexamen motor verkeersdeelneming A getrapt"),
    A_T("Tussentijdse toets motor verkeersdeelneming A"),
    B("Praktijkexamen personenauto"),
    BE("Personenauto met Aanhangwagen"),
    BE_T("Tussentijdse toets personenauto met aanhangwagen"),
    BTH("Theorie-examen personenauto"),
    B_RT("RIS-toets personenauto"),
    B_T("Tussentijdse toets personenauto"),
    C("Vrachtauto C praktijk (C)"),
    C1("Vrachtauto C1 praktijk (C1)"),
    C1E("Vrachtauto C1 met aanhangwagen praktijk (C1E)"),
    CE("Vrachtauto C met aanhangwagen praktijk (CE)"),
    D("Bus D praktijk (D)"),
    DE("Bus D met aanhangwagen praktijk (DE)"),
    HEFP("Vorkheftruck praktijk (HEFP)"),
    HEFT("Vorkheftruck theorie (HEFT)"),
    LZV("Lange en zware voertuigen (praktijk) (LZV)"),
    R2C("Vrachtauto rijbewijs  - theorie deel 2, administratie (R2C)"),
    R2D("Bus  rijbewijs - theorie deel 2, administratie (R2D)"),
    REP("Reachtruck praktijk (REP)"),
    RV1("Vrachtauto C / Bus D  theorie deel 1, Verkeer en Techniek (RV1)"),
    RV1L("Vrachtauto C1/ Bus D1  - theorie deel 1, Verkeer en Techniek light (RV1L)"),
    RV1P("Vrachtauto C/ Bus D  - theorie deel 1, Verkeer en Techniek plus (RV1P)"),
    T("T-rijbewijs praktijk"),
    TAP("Taxi Amsterdam praktijk (TAP)"),
    TAT("Taxi Amsterdam theorie (TAT)"),
    TVP("Taxi vakbekwaamheid praktijk volledig (TVP)"),
    TVPC("Taxi vakbekwaamheid praktijk beperkt (TVPC)"),
    TVT("Taxi vakbekwaamheid theorie (TVT)"),
    T_TH("T-rijbewijs theorie"),
    V2C("Vrachtauto rijbewijs met code 95 - theorie deel 2, administratie (V2C)"),
    V2D("Bus rijbewijs met code 95- theorie deel 2, administratie (V2D)"),
    V3C("Vrachtauto rijbewijs met code 95- theorie deel 3, administratie cases (V3C)"),
    V3D("Bus rijbewijs met code 95 - theorie deel 3, administratie cases (V3D)"),
    VVW("Veiligheidsadviseur wegvervoer verlenging (VVW)"),
    VW("Veiligheidsadviseur wegvervoer (VW)"),
}

val Categorie.producten
    get() = Product.values().filter { it.categorie == this }