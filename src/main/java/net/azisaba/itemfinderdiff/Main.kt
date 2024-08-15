@file:JvmName("MainKt")
package net.azisaba.itemfinderdiff

import org.apache.commons.csv.CSVFormat
import java.io.File

fun main(args: Array<String>) {
    val file1 = File(args[0])
    if (!file1.exists()) {
        println("File not found: ${file1.absolutePath}")
        return
    }
    val file2 = File(args[1])
    if (!file2.exists()) {
        println("File not found: ${file2.absolutePath}")
        return
    }
    val base = CSVFormat.EXCEL.parse(file1.reader()).map { it.values() }
    val compare = CSVFormat.EXCEL.parse(file2.reader()).map { it.values() }
    if (args.getOrNull(2) == "l") {
        // compare locations csv
        // Location, Amount, Type, Item name, Item name with color
        val builder = CsvBuilder("AddedRemoved", "Location", "Amount", "Type", "Name", "NameWithColor")
        base.forEach { baseRow ->
            val compareRow = compare.find { it[0] == baseRow[0] && it[3] == baseRow[3] && it[4] == baseRow[4] }
            if (compareRow == null) {
                builder.add("Removed", baseRow[0], baseRow[1], baseRow[2], baseRow[3], baseRow[4])
            } else {
                val baseAmount = baseRow[1].toLongOrNull() ?: 0
                val compareAmount = compareRow[1].toLongOrNull() ?: 0
                if (baseAmount != compareAmount) {
                    builder.add("Changed", baseRow[0], (compareAmount - baseAmount).toString(), baseRow[2], baseRow[3], baseRow[4])
                }
            }
        }
        compare.forEach { compareRow ->
            val baseRow = base.find { it[0] == compareRow[0] && it[3] == compareRow[3] && it[4] == compareRow[4] }
            if (baseRow == null) {
                builder.add("Added", compareRow[0], compareRow[1], compareRow[2], compareRow[3], compareRow[4])
            }
        }
        File("diff-locations.csv").writeText(builder.build())
        return
    }
    val builder = CsvBuilder("Amount", "Type", "Name", "NameWithColor")
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
