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

val linksRegistry = HashMap<String, Pair<Any, KClass<Any>>>()

class Link(private val linkName: String) {
    //- User(link(NAME).forValue(1))
    fun <T : Any> forValue(value: T) : T {
        linksRegistry[this.linkName] = Pair(value, value::class)
        return value
    }
}

fun from(linkName: String) : Any {
    return linksRegistry.getValue(linkName)
}

//а тот кто пользуется ссылкой:
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



//    provider.save(
//        SomeDatabaseTableRecord("first record", Date()),
//    )
}
