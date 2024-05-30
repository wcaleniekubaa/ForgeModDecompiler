package com.github.wcaleniekubaa.fmd

import org.benf.cfr.reader.Main
import org.jd.core.v1.ClassFileToJavaSourceDecompiler
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler
import java.io.File
import java.util.jar.JarInputStream
import kotlin.io.path.Path

//import org.jd.core.v1.api.Decompiler;

enum class Decompiler(val executor: (File) -> Unit) {
    // JD Decompiler
    JD(executor = { input ->
        val printer = StringBuilderPrinter()

        val file = File(input.absolutePath)
        val loader = ZipLoader(file)
        //ClassFileToJavaSourceDecompiler().decompile(loader, printer, input.absolutePath)
        //val stringBuffer: java.lang.StringBuilder = printer.stringBuffer

        val inputStream = java.util.jar.JarInputStream(java.io.File("temp/ff_in.jar").inputStream())
        var entry = inputStream.nextJarEntry
        while (entry != null) {
            val tmp_name = entry.name.replace(".class", ".java")
            val file = java.io.File("Sources/${tmp_name}")

            if (file.parentFile != null) file.parentFile.mkdirs()
            if (file.isDirectory) {
                entry = inputStream.nextJarEntry
                continue
            }
            if (entry.isDirectory) {
                entry = inputStream.nextJarEntry
                continue
            }
            println("Processing ${entry.name} entry!")
            if (!entry.name.endsWith(".class")) {
                entry = inputStream.nextJarEntry
                continue
            }
            ClassFileToJavaSourceDecompiler().decompile(loader, printer, entry.name)
            val stringBuffer: java.lang.StringBuilder = printer.stringBuffer

            val outputStream = file.outputStream()
            outputStream.write(stringBuffer.toString().toByteArray())
            outputStream.flush()
            outputStream.close()

            entry = inputStream.nextJarEntry
        }
        inputStream.close()
    }),

    // CFR Decompiler
    CFR(executor = { input ->
        Main.main(arrayOf(input.absolutePath, "--outputdir", "Sources/"))
    }),

    // Procyon Decompiler
    PROCYON(executor = { input ->
        com.strobel.decompiler.DecompilerDriver.main(arrayOf("-jar", input.absolutePath, "-o", "Sources/"))

    }),
    // Fernflower Decompiler
    FERNFLOWER(executor = { input ->
        File("temp/ff_temp").mkdirs()
        ConsoleDecompiler.main(arrayOf("temp/ff_in.jar", "temp/ff_temp"))
        val inputStream = JarInputStream(File("temp/ff_temp/ff_in.jar").inputStream())
        var entry = inputStream.nextJarEntry
        while (entry != null) {
            val file = File("Sources/${entry.name}")

            if (file.parentFile != null) file.parentFile.mkdirs()
            if (entry.isDirectory) {
                val executionPath = System.getProperty("user.dir")
                java.nio.file.Files.createDirectory(Path(file.absolutePath))
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
