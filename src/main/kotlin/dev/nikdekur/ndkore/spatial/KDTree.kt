//package dev.nikdekur.ndkore.spatial
//
//class KDNode<T>(val x: Int, val y: Int, val z: Int, val data: T, val axis: Int) {
//    var left: KDNode<T>? = null
//    var right: KDNode<T>? = null
//}
//
//class KDTree<T> {
//    var root: KDNode<T>? = null
//
//    fun insert(x: Int, y: Int, z: Int, data: T) {
//        root = insert(root, x, y, z, data, 0)
//    }
//
//    private fun insert(node: KDNode<T>?, x: Int, y: Int, z: Int, data: T, depth: Int): KDNode<T> {
//        if (node == null) {
//            return KDNode(x, y, z, data, depth % 3)
//        }
//
//        var cmp = 0
//        when (node.axis) {
//            0 -> cmp = x.compareTo(node.x)
//            1 -> cmp = y.compareTo(node.y)
//            2 -> cmp = z.compareTo(node.z)
//        }
//
//        if (cmp < 0) {
//            node.left = insert(node.left, x, y, z, data, depth + 1)
//        } else {
//            node.right = insert(node.right, x, y, z, data, depth + 1)
//        }
//
//        return node
//    }
//
//    fun findNear(x: Int, y: Int, z: Int, radius: Double): HashSet<T> {
//        val result = HashSet<T>()
//        findNear(root, x, y, z, radius, result)
//        return result
//    }
//
//    private fun findNear(node: KDNode<T>?, x: Int, y: Int, z: Int, radius: Double, result: HashSet<T>) {
//        if (node == null) {
//            return
//        }
//
//        val d = distanceSquared(x, y, z, node.x, node.y, node.z)
//        if (d <= radius * radius) {
//            result.add(node.data)
//        }
//
//        var cmp = 0
//        when (node.axis) {
//            0 -> cmp = x.compareTo(node.x)
//            1 -> cmp = y.compareTo(node.y)
//            2 -> cmp = z.compareTo(node.z)
//        }
//
//        if (cmp < 0) {
//            findNear(node.left, x, y, z, radius, result)
//            if (d <= radius * radius) {
//                findNear(node.right, x, y, z, radius, result)
//            }
//        } else {
//            findNear(node.right, x, y, z, radius, result)
//            if (d <= radius * radius) {
//                findNear(node.left, x, y, z, radius, result)
//            }
//        }
//    }
//
//    private fun distanceSquared(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Double {
//        val dx = x1 - x2
//        val dy = y1 - y2
//        val dz = z1 - z2
//        return (dx * dx + dy * dy + dz * dz).toDouble()
//    }
//}