package com.white_horse.photocollage.models

import com.white_horse.photocollage.view.polygon.PolygonView

data class ViewTree(
    val polygonView: PolygonView,
    val id: String = polygonView.getUniqueId(),
    var left: ViewTree? = null,
    var right: ViewTree? = null
) {
    fun clearChildren() {
        left = null
        right = null
    }
}