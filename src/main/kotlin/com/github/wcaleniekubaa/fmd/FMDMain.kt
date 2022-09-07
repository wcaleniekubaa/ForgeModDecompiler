package com.github.wcaleniekubaa.fmd

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme
import com.github.wcaleniekubaa.fmd.asm.SeargeMapper
import com.github.wcaleniekubaa.fmd.ui.FMDUI
import com.opencsv.CSVReader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import java.io.File
import java.io.FileNotFoundException
import java.io.FilenameFilter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import javax.swing.UIManager

object FMDMain {
    @JvmStatic
    val logger: Logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME)

    @JvmStatic
    val mappingsFiles = mutableListOf<File>()

    @JvmStatic
    fun main(args: Array<String>) {

        val mappingsFile = File("mappings")
        val filter = FilenameFilter { dir, name ->
            File(dir, name).isDirectory
        }
        val list = mappingsFile.listFiles(filter)
        mappingsFiles.addAll(list!!)

        println(mappingsFile)

        // Want to change theme?
        // Change it here
        UIManager.setLookAndFeel(FlatGitHubDarkIJTheme())
        FMDUI()
    }

    @JvmStatic
    fun process(mappings: File, input: File, decompiler: Decompiler) {
        logger.info("Started mapping jar: $input with mappings: $mappings")
        val fields = File(mappings, "fields.csv")
        if(!fields.exists())
            throw FileNotFoundException("fields.csv file does not exist.")
        val methods = File(mappings,"methods.csv")
        if(!fields.exists())
            throw FileNotFoundException("methods.csv file does not exist.")

        val methodList = mutableListOf<MCPMember>()
        Files.newBufferedReader(Paths.get(methods.toURI())).use { reader ->
            CSVReader(reader).use { csvReader ->
                var line: Array<String?>?
                while (csvReader.readNext().also { line = it } != null) {
                    methodList.add(MCPMember(line?.get(0) ?: "", line?.get(1) ?: ""))
                }
            }
        }
        val fieldList = mutableListOf<MCPMember>()
        Files.newBufferedReader(Paths.get(fields.toURI())).use { reader ->
            CSVReader(reader).use { csvReader ->
                var line: Array<String?>?
                while (csvReader.readNext().also { line = it } != null) {
                    fieldList.add(MCPMember(line?.get(0) ?: "", line?.get(1) ?: ""))
                }
            }
        }

        if(!input.exists())
            throw IllegalArgumentException("Input file does not exist.")

        val mappings = MCPMappings(methodList, fieldList)

        val inputStream = JarInputStream(input.inputStream())
        File("temp").mkdirs()
        val outputStream = JarOutputStream(File("temp/ff_in.jar").outputStream())

        var entry = inputStream.nextJarEntry
        while(entry != null) {

            logger.info("Reading entry: " + entry.name)

            if(entry.name.endsWith(".class")) {
                val cr = ClassReader(inputStream)
                val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
                val sm = SeargeMapper(mappings)
                cr.accept(ClassRemapper(cw, sm), ClassReader.EXPAND_FRAMES)

                logger.info("Saving class entry: ${entry.name}")

                outputStream.putNextEntry(JarEntry(entry.name))
                outputStream.write(cw.toByteArray())
                outputStream.closeEntry()
            } else {

                logger.info("Saving non-class entry: ${entry.name}")

                outputStream.putNextEntry(JarEntry(entry.name))
                outputStream.write(inputStream.readBytes())
                outputStream.closeEntry()
            }
            entry = inputStream.nextJarEntry
        }
        outputStream.close()

        decompiler.executor.invoke(File("temp/ff_in.jar"))
    }
}