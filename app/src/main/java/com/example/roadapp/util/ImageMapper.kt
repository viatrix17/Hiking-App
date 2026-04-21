package com.example.roadapp.util

import com.example.roadapp.R

fun getRouteImageId(routeId: Int): Int {
    return when (routeId) {
        1 -> R.drawable.trasa_1_dookola_tatr
        2 -> R.drawable.trasa_2_velo_czorsztyn
        3 -> R.drawable.trasa_3_singletrack_glacensis
        4 -> R.drawable.trasa_4_szlak_wokol_sniezki
        5 -> R.drawable.trasa_5_beskidzkie_grzbiety
        6 -> R.drawable.trasa_6_dolina_pieciu_stawow
        7 -> R.drawable.trasa_7_sokolica_trzy_korony
        8 -> R.drawable.trasa_8_czerwone_wierchy
        9 -> R.drawable.trasa_9_sniezne_kotly
        else -> R.drawable.ic_placeholder
    }
}