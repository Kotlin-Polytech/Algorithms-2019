package lesson3

import java.lang.NullPointerException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>>() : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    private var fromElement: T? = null
    private var toElement: T? = null

    private constructor(root: Node<T>?, fromElement: T?, toElement: T?) : this() {
        this.root = root
        this.fromElement = fromElement
        this.toElement = toElement
    }

    override var size = 0
        get() {
            var result = 0
            for (i in this)
                if (sequence(i))
                    result++
            return result
        }
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    /*
       Helper for headSet, subSet and tailSet
       TimeComplexity =O(1)
       MemoryComplexity =O(1)
     */
    private fun sequence(value: T): Boolean {
        var result = false
        when {
            (fromElement == null || value >= fromElement!!)
                    && (toElement == null || value < toElement!!)
            -> result = true
        }
        return result
    }


    override fun add(element: T): Boolean {

        when {
            !sequence(element) -> throw IllegalArgumentException()
            else -> {
                val closest = find(element)
                val comparison = if (closest == null) -1 else element.compareTo(closest.value)
                if (comparison == 0) {
                    return false
                }
                val newNode = Node(element)
                when {
                    closest == null -> root = newNode
                    comparison < 0 -> {
                        assert(closest.left == null)
                        closest.left = newNode
                    }
                    else -> {
                        assert(closest.right == null)
                        closest.right = newNode
                    }
                }
                size++
                return true
            }
        }

    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    override fun height(): Int = height(root)

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    /*
     little helper to simplify remove() method
     TimeComplexity =O(LogN)
     MemoryComplexity =O(1)
    */
    private fun parents(node: Node<T>): Node<T> {
        var parentLeaf = root ?: throw NullPointerException("Expression 'root' must not be null")
        var next: Node<T>?

        when {
            node.value > parentLeaf.value -> next = parentLeaf.right
            else -> next = parentLeaf.left
        }

        while (next != node && next != null) {
            parentLeaf = next
            when {
                node.value > parentLeaf.value -> next = parentLeaf.right
                else -> next = parentLeaf.left
            }
        }
        return parentLeaf
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     * T=O(logN) or T=O(N) not sure
     * M=O(1)
     * N=nodes.count()
     */
    override fun remove(element: T): Boolean {
        val elToRemove = find(element)
        var result = false

        if (elToRemove != null && elToRemove.value == element) {

            var remR = elToRemove.right
            var remL = elToRemove.left

            fun rotate(replaceable: Node<T>) {
                replaceable.left = remL
                replaceable.right = remR

                if (elToRemove != root) {
                    val parentRem = parents(elToRemove)
                    if (parentRem.right != elToRemove)
                        parentRem.left = replaceable
                    else
                        parentRem.right = replaceable
                } else
                    root = replaceable
            }

            if (elToRemove.right == null) {

                if (elToRemove.left != null) {
                    var maxLeft = elToRemove.left ?: throw NullPointerException()
                    var parent = maxLeft

                    while (maxLeft.right != null) {
                        parent = maxLeft
                        maxLeft = maxLeft.right ?: throw NullPointerException()
                    }

                    if (maxLeft == remL) {
                        remL = maxLeft.left
                    } else parent.right = maxLeft.left

                    rotate(maxLeft)
                } else
                    if (elToRemove != root) {
                        val parentRem = parents(elToRemove)
                        if (parentRem.right == elToRemove) {
                            parentRem.right = null
                        } else
                            parentRem.left = null
                    } else
                        root = null
            } else {
                if (elToRemove.right != null) {
                    var minRight = elToRemove.right
                    var parent = minRight

                    if (minRight != null) {
                        while (minRight?.left != null) {
                            parent = minRight
                            minRight = minRight.left ?: throw NullPointerException()
                        }
                    }

                    if (minRight != null) {
                        if (minRight == remR) {
                            remR = minRight.right
                        } else
                            if (parent != null) {
                                parent.left = minRight.right
                            }
                        rotate(minRight)
                    }
                }
            }
            result = true
        } else
            return result
        size--
        return result
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0 && sequence(closest.value)
    }

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {
        private var current: Node<T>? = null
        private val stack: Stack<Node<T>> = Stack()

        init {
            var node = root
            while (node != null) {
                stack += node
                node = node.left
            }
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         * T=O(1)
         * M=O(1)
         */
        override fun hasNext(): Boolean = stack.isNotEmpty()


        /**
         * Поиск следующего элемента
         * Средняя
         * T=O(LogN)
         * M=O(LogN)
         * N=nodes.count()
         */
        override fun next(): T {
            if (hasNext()) {
                var last = stack.pop()
                val new = last

                if (last.right != null) {
                    last = last.right
                    while (last != null) {
                        stack += last
                        last = last.left
                    }
                }
                current = new
                return new.value
            }
            throw NoSuchElementException()
        }

        /**
         * Удаление следующего элемента
         * Сложная
         * T=O(logN)
         * M=O(1)
         * N=nodes.count()
         */
        override fun remove() {
            if (current == null) throw NoSuchElementException()

            val remR = current!!.right
            if (remR?.left != null) {
                var replaceable = remR.left
                var nodeToReplace = replaceable

                if (replaceable != null) {
                    while (replaceable?.left != null)
                        replaceable = replaceable.left
                }

                while (nodeToReplace != remR)
                    nodeToReplace = stack.pop()

                stack += replaceable
            }
            if (current != null)
                remove(current?.value)
        }
    }


    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     * T=O(1)
     * M=O(1)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        val node = root
        if (node != null) {
            if (node.value < toElement && node.left != null)
                node.left
        }
        return KtBinaryTree(root, fromElement, toElement)
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     * T=O(1)
     * M=O(1)
     */
    override fun headSet(toElement: T): SortedSet<T> {
        val node = root
        if (node != null) {
            if (node.value < toElement && node.left != null)
                node.left
        }
        return KtBinaryTree(root, null, toElement)
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     * T=O(1)
     * M=O(1)
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        val node = root
        if (node != null) {
            if (node.value > fromElement && node.right != null)
                node.right
        }
        return KtBinaryTree(root, fromElement, null)
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
