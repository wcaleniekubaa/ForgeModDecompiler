/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package com.github.wcaleniekubaa.fmd

import org.jd.core.v1.api.loader.Loader
import org.jd.core.v1.api.loader.LoaderException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.zip.ZipInputStream


class ZipLoader(zip: File?) : Loader {
    protected var map: HashMap<String, ByteArray> = HashMap()

    init {
        val buffer = ByteArray(1024 * 2)

        try {
            ZipInputStream(FileInputStream(zip)).use { zis ->
                var ze = zis.nextEntry
                while (ze != null) {
                    if (ze.isDirectory == false) {
                        val out = ByteArrayOutputStream()
                        var read = zis.read(buffer)

                        while (read > 0) {
                            out.write(buffer, 0, read)
                            read = zis.read(buffer)
                        }

                        map[ze.name] = out.toByteArray()
                    }

                    ze = zis.nextEntry
                }
                zis.closeEntry()
            }
        } catch (e: IOException) {
            throw LoaderException(e)
        }
    }

    @Throws(LoaderException::class)
    override fun load(internalName: String): ByteArray {
        println("Loading $internalName from jar!")
        return map["$internalName"]!!
    }

    override fun canLoad(internalName: String): Boolean {
        return map.containsKey("$internalName.class")
    }
}