package com.github.wcaleniekubaa.fmd

import org.benf.cfr.reader.Main
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler
import java.io.File
import java.util.jar.JarInputStream

enum class Decompiler(val executor: (File) -> Unit) {
    CFR(executor = { input ->
        Main.main(arrayOf(input.absolutePath, "--outputdir", "Sources/"))
    }),
    PROCYON(executor = { input ->
        com.strobel.decompiler.DecompilerDriver.main(arrayOf("-jar", input.absolutePath, "-o", "Sources/"))

    }),
    FERNFLOWER(executor = { input ->
        File("temp/ff_temp").mkdirs()
        ConsoleDecompiler.main(arrayOf("temp/ff_in.jar", "temp/ff_temp"))
        val inputStream = JarInputStream(File("temp/ff_temp/ff_in.jar").inputStream())
        var entry = inputStream.nextJarEntry
        while (entry != null) {
            val file = File("Sources/${entry.name}")

            if (file.parentFile != null) file.parentFile.mkdirs()
            if (file.isDirectory) {
                entry = inputStream.nextJarEntry
                continue
            }
            val outputStream = file.outputStream()
            outputStream.write(inputStream.readBytes())
            outputStream.close()

            entry = inputStream.nextJarEntry
        }
        inputStream.close()

    })
}
