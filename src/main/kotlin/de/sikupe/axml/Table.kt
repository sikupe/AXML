package de.sikupe.axml

abstract class Table<T>: Bytable {
    protected val mSet = HashSet<T>()
    val mSize: Int
        get() = mSet.size

    /**
     * Adds the passed element to the table. Duplicates are not saved multiple times
     * @param element element to save
     * @return Returns the ID within the table of the added element
     */
    fun add(element: T): Int {
        return if (mSet.add(element)) {
            mSize - 1
        } else {
            mSet.indexOf(element)
        }
    }
}