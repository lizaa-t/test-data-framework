package org.example

import java.util.Date
import kotlin.reflect.KClass

interface Entry {
}

class SomeDatabaseTableRecord(val col1: String, val col2: Date) : Entry {}

interface Provider {
    fun save(entry: Entry)
    fun clear()
}

class SomeDatabaseProvider : Provider {
    override fun save(entry: Entry) {
        println("save $entry to db")
    }

    override fun clear() {
        println("clear db")
    }
}

abstract class Storage(val provider: Provider) {
    val entries: MutableList<Entry> = mutableListOf()

    operator fun Entry.unaryMinus() {
        this@Storage.entries.add(this)
    }
}

class SomeDatabaseStorage(provider: Provider) : Storage(provider) {

}

val linksRegistry = HashMap<String, HashMap<Enum<*>, Any>>()

class Link<T: Enum<T>>(private val linkName: T) {
    fun forValue(value: Any): Any {
        val classQualifiedName = value::class.qualifiedName!!
        linksRegistry.putIfAbsent(classQualifiedName, HashMap())
        linksRegistry[classQualifiedName]!![linkName] = value
        return value
    }
}

inline fun <reified T> from(linkName: Enum<*>) : T {
    val classQualifiedName = T::class.qualifiedName!!
    return linksRegistry.getValue(classQualifiedName).getValue(linkName) as T
}


//- User(link(NAME).forValue(1))
//- Course(userId = from(NAME))

enum class LinkNames {
    ONE_LINK, TWO_LINK
}

fun record(storage: Storage, action: Storage.() -> Unit): Storage {
    storage.apply(action)
    return storage
}

fun main() {
    val provider = SomeDatabaseProvider()
    val storage = SomeDatabaseStorage(provider)

    val linkValue = Link(LinkNames.ONE_LINK).forValue(1)
    val link = from<Int>(LinkNames.ONE_LINK)

    val case = record(storage) {
        - SomeDatabaseTableRecord("third record", Date())
        - SomeDatabaseTableRecord("fourth record", Date())
    }
    println(case.entries.first())

//    provider.save(
//        SomeDatabaseTableRecord("first record", Date()),
//    )
}
