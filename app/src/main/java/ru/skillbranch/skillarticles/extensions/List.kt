package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<List<Pair<Int, Int>>> {
    val result = mutableListOf<List<Pair<Int, Int>>>()
    var position = 0
    var tempResult: Pair<Int, Int>? = null
    for (bound in bounds) {
        val boundResultList = mutableListOf<Pair<Int, Int>>()

        loop@ for (index: Int in position until this.size) {
            if (tempResult != null) {
                when {
                    tempResult.second < bound.second -> {
                        boundResultList.add(
                            Pair(bound.first, tempResult.second)
                        )
                        tempResult = null
                    }
                    tempResult.second == bound.second -> {
                        boundResultList.add(
                            Pair(bound.first, tempResult.second)
                        )
                        tempResult = null
                        break@loop
                    }
                    tempResult.first >= bound.first
                            && tempResult.second >= bound.second
                            && tempResult.first < bound.second -> {
                        boundResultList.add(Pair(bound.first, bound.second))
                        tempResult = Pair(bound.second, tempResult.second)
                        break@loop
                    }
                }
            }
            when {
                this[index].second < bound.second -> {
                    boundResultList.add(this[index])
                    position++
                }
                this[index].second == bound.second -> {
                    boundResultList.add(this[index])
                    position++
                    break@loop
                }
                this[index].second >= bound.second && this[index].first < bound.second -> {
                    boundResultList.add(Pair(this[index].first, bound.second))
                    tempResult = Pair(bound.second, this[index].second)
                    position++
                    break@loop
                }
            }
        }
        result.add(boundResultList)
    }
    return result
}

