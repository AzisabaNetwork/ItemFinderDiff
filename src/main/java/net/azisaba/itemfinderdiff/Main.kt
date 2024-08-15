@file:JvmName("MainKt")
package net.azisaba.itemfinderdiff

import org.apache.commons.csv.CSVFormat
import java.io.File

fun main(args: Array<String>) {
    val file1 = File(args.getOrNull(0))
    if (!file1.exists()) {
        println("File not found: ${file1.absolutePath}")
        return
    }
    val file2 = File(args.getOrNull(1))
    if (!file2.exists()) {
        println("File not found: ${file2.absolutePath}")
        return
    }
    val builder = CsvBuilder("Amount", "Type", "Name", "NameWithColor")
    val base = CSVFormat.EXCEL.parse(file1.reader()).map { it.values() }
    val compare = CSVFormat.EXCEL.parse(file2.reader()).map { it.values() }
    val baseMap = base.associate { it[3] to (it[0].toLongOrNull() ?: 0L) }
    val compareMap = compare.associate { it[3] to (it[0].toLongOrNull() ?: 0L) }
    val allKeys = baseMap.keys + compareMap.keys
    allKeys.mapNotNull { key ->
        val baseAmount = baseMap[key] ?: 0
        val compareAmount = compareMap[key] ?: 0
        if (baseAmount != compareAmount) {
            ItemData(
                (compareAmount - baseAmount),
                base.find { it[3] == key }?.get(1) ?: compare.find { it[3] == key }?.get(1) ?: "Unknown",
                base.find { it[3] == key }?.get(2) ?: compare.find { it[3] == key }?.get(2) ?: "Unknown",
                key
            )
        } else {
            null
        }
    }.sortedByDescending { it.amount }.forEach {
        builder.add(it.amount.toString(), it.type, it.name, it.nameWithColor)
    }
    File("diff.csv").writeText(builder.build())
}
