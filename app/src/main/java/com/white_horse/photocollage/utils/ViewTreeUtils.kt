package com.white_horse.photocollage.utils

import com.white_horse.photocollage.models.ChildPolygonsData
import com.white_horse.photocollage.models.ViewTree
import com.white_horse.photocollage.view.polygon.PolygonView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

suspend fun addChildren(rootTree: ViewTree, childData: ChildPolygonsData) {
    withContext(Dispatchers.Default) {
        val viewQueue = LinkedList<ViewTree>()
        viewQueue.add(rootTree)

        while (!viewQueue.isEmpty()) {
            val temp = viewQueue.pop()
            val left = temp.left
            val right = temp.right
            if (temp.id == childData.parentPolygonId) {
                temp.left = ViewTree(childData.child1)
                temp.right = ViewTree(childData.child2)
                viewQueue.clear()
                break
            }

            if (left != null) {
                viewQueue.add(left)
            }

            if (right != null) {
                viewQueue.add(right)
            }
        }
    }
}

suspend fun clearParentsChildren(parentId : Int, rootTree: ViewTree) {
    withContext(Dispatchers.Default) {
        val viewQueue = LinkedList<ViewTree>()
        viewQueue.add(rootTree)

        while (!viewQueue.isEmpty()) {
            val temp = viewQueue.pop()
            val left = temp.left
            val right = temp.right
            if (temp.id == parentId) {
                temp.clearChildren()
                viewQueue.clear()
                break
            }

            if (left != null) {
                viewQueue.add(left)
            }

            if (right != null) {
                viewQueue.add(right)
            }
        }
    }
}

suspend fun getActivePolygonsList(rootTree: ViewTree): List<PolygonView> {
    return withContext(Dispatchers.Default) {
        val activeViews = ArrayList<PolygonView>()
        val viewQueue = LinkedList<ViewTree>()
        viewQueue.add(rootTree)

        while (!viewQueue.isEmpty()) {
            val temp = viewQueue.pop()
            val left = temp.left
            val right = temp.right
            if (temp.left == null && temp.right == null) {
                activeViews.add(temp.polygonView)
            }

            if (left != null) {
                viewQueue.add(left)
            }

            if (right != null) {
                viewQueue.add(right)
            }
        }
        activeViews
    }
}

suspend fun getAllPolygonViewList(rootTree: ViewTree): List<PolygonView> {
    return withContext(Dispatchers.Default) {
        val allViews = ArrayList<PolygonView>()
        val viewQueue = LinkedList<ViewTree>()
        viewQueue.add(rootTree)

        while (!viewQueue.isEmpty()) {
            val temp = viewQueue.pop()
            val left = temp.left
            val right = temp.right
            allViews.add(temp.polygonView)

            if (left != null) {
                viewQueue.add(left)
            }

            if (right != null) {
                viewQueue.add(right)
            }
        }
        allViews
    }
}