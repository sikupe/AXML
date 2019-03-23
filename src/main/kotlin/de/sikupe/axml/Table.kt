package de.sikupe.axml

abstract class Table<T>: Bytable {
    protected val mSet = mutableListOf<T>()
    val mSize: Int
        get() = mSet.size

    /**
     * Adds the passed element to the table. Duplicates are not saved multiple times
     * @param element element to save
     * @return Returns the ID within the table of the added element
     */
    fun addOriginal(element: T): Int {
        mSet.add(element)
        return mSize - 1
    }

    fun addAdditional(element: T): Int {
        return if(mSet.contains(element)) {
            mSet.indexOf(element)
        } else {
            mSet.add(element)
            mSize - 1
        }
    }
}