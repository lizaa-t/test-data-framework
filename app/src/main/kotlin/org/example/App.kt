package org.example

import java.util.Date
import kotlin.reflect.KClass

interface Entry

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

interface Storage {
    val provider: Provider
    val entries: List<Entry>

    fun save_entries() {
        println("save entries in storage")
//        provider.save(this.entries.pop(), *this.entries)
    }
}

class SomeDatabaseStorage(
    override val provider: Provider,
    override val entries: List<Entry>
) : Storage {

}

// class-wrapper instead of Any
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

//fun record(storage: Storage, operation: (Entry) -> Entry): Storage {
//    return storage
//}

enum class LinkNames {
    ONE_LINK, TWO_LINK
}

fun main() {
    val provider = SomeDatabaseProvider()
    val storage = SomeDatabaseStorage(
        provider,
        listOf(
            SomeDatabaseTableRecord("first record", Date()),
            SomeDatabaseTableRecord("second record", Date())
        )
    )
    val linkValue = Link(LinkNames.ONE_LINK).forValue(1)
    println(linkValue)
    val link = from<Int>(LinkNames.ONE_LINK)
    println(link)

//    provider.save(
//        SomeDatabaseTableRecord("first record", Date()),
//    )
}
