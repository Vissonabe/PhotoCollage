package com.white_horse.photocollage.activity

import com.white_horse.photocollage.models.ChildPolygonsData
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.ViewTree
import com.white_horse.photocollage.view.polygon.PolygonView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class ViewTreeManager(private val handleUndoFABVisibility : (Boolean) -> Unit) {

    lateinit var rootViewTree: ViewTree
    private val undoStack = Stack<List<ViewTree>>()

    private fun pushIntoUndoStack(items :List<ViewTree>) {
        undoStack.push(items)
        if(undoStack.size == 1) {
            handleUndoFABVisibility.invoke(true)
        }
    }

    private fun popFromUndoStack() : List<ViewTree> {
        val pop = undoStack.pop()
        if(undoStack.isEmpty()) {
            handleUndoFABVisibility.invoke(false)
        }
        return pop
    }

    fun initViewTree(tree: ViewTree) {
        rootViewTree = tree
    }

    suspend fun addChildren(childData: ChildPolygonsData) {
        withContext(Dispatchers.Default) {
            val viewQueue = LinkedList<ViewTree>()
            viewQueue.add(rootViewTree)

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

    suspend fun clearParentsChildren(parentId: String) {
        withContext(Dispatchers.Default) {
            val viewQueue = LinkedList<ViewTree>()
            viewQueue.add(rootViewTree)

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

    private suspend fun getActivePolygonsList(): List<ViewTree> {
        return withContext(Dispatchers.Default) {
            val activeViews = ArrayList<ViewTree>()
            val viewQueue = LinkedList<ViewTree>()
            viewQueue.add(rootViewTree)

            while (!viewQueue.isEmpty()) {
                val temp = viewQueue.pop()
                val left = temp.left
                val right = temp.right
                if (temp.left == null && temp.right == null) {
                    activeViews.add(temp)
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

    suspend fun splitActivePolygonsList(start: Point, end: Point) {
        val activeList = getActivePolygonsList()
        if(activeList.isNotEmpty()) {
            pushIntoUndoStack(activeList)
            activeList.forEachIndexed { index, viewTree ->
                viewTree.polygonView.splitView(start, end)
            }
        }
    }

    suspend fun clearActivePolygonsUndoAction() {
        withContext(Dispatchers.Default) {
            if(undoStack.isNotEmpty()) {
                val activeViewTreeList = popFromUndoStack()
                undoActivePolygons(rootViewTree, activeViewTreeList)
            }
        }
    }

    private suspend fun undoActivePolygons(rootTree: ViewTree?, viewTrees: List<ViewTree>) {
        withContext(Dispatchers.Default) {
            if (rootTree == null) {
                return@withContext
            }
            val viewQueue = LinkedList<ViewTree>()
            viewQueue.add(rootViewTree)

            while (!viewQueue.isEmpty()) {
                val temp = viewQueue.pop()
                val left = temp.left
                val right = temp.right

                viewTrees.forEachIndexed { index, viewTree ->
                    if (temp.id == viewTree.id) {
                        temp.clearChildren()
                        withContext(Dispatchers.Main) {
                            temp.polygonView.clearPolygonChildrens()
                        }
                    }
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

    suspend fun getAllPolygonViewList(): List<PolygonView> {
        return withContext(Dispatchers.Default) {
            val allViews = ArrayList<PolygonView>()
            val viewQueue = LinkedList<ViewTree>()
            viewQueue.add(rootViewTree)

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
}