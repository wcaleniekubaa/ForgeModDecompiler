/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package com.github.wcaleniekubaa.fmd

import org.jd.core.v1.api.printer.Printer

class StringBuilderPrinter : Printer {
    var stringBuffer: StringBuilder = StringBuilder(10 * 1024)
        protected set

    protected var unicodeEscape: Boolean = true
    protected var realignmentLineNumber: Boolean = false

    var majorVersion: Int = 0
        protected set
    var minorVersion: Int = 0
        protected set
    protected var indentationCount: Int = 0

    protected fun escape(s: String?) {
        if (unicodeEscape && (s != null)) {
            val length = s.length

            for (i in 0 until length) {
                val c = s[i]

                if (c == '\t') {
                    stringBuffer.append(c)
                } else if (c.code < 32) {
                    // Write octal format
                    stringBuffer.append("\\0")
                    stringBuffer.append(('0'.code + (c.code shr 3)).toChar())
                    stringBuffer.append(('0'.code + (c.code and 0x7)).toChar())
                } else if (c.code > 127) {
                    // Write octal format
                    stringBuffer.append("\\u")

                    var z = (c.code shr 12)
                    stringBuffer.append((if ((z <= 9)) ('0'.code + z) else (('A'.code - 10) + z)).toChar())
                    z = ((c.code shr 8) and 0xF)
                    stringBuffer.append((if ((z <= 9)) ('0'.code + z) else (('A'.code - 10) + z)).toChar())
                    z = ((c.code shr 4) and 0xF)
                    stringBuffer.append((if ((z <= 9)) ('0'.code + z) else (('A'.code - 10) + z)).toChar())
                    z = (c.code and 0xF)
                    stringBuffer.append((if ((z <= 9)) ('0'.code + z) else (('A'.code - 10) + z)).toChar())
                } else {
                    stringBuffer.append(c)
                }
            }
        } else {
            stringBuffer.append(s)
        }
    }

    // --- Printer --- //
    override fun start(maxLineNumber: Int, majorVersion: Int, minorVersion: Int) {
        stringBuffer.setLength(0)
        this.majorVersion = majorVersion
        this.minorVersion = minorVersion
        this.indentationCount = 0
    }

    override fun end() {}

    override fun printText(text: String) {
        escape(text)
    }

    override fun printNumericConstant(constant: String) {
        escape(constant)
    }

    override fun printStringConstant(constant: String, ownerInternalName: String?) {
        escape(constant)
    }

    override fun printKeyword(keyword: String) {
        stringBuffer.append(keyword)
    }

    override fun printDeclaration(type: Int, internalTypeName: String?, name: String?, descriptor: String?) {
        escape(name)
    }

    override fun printReference(
        type: Int,
        internalTypeName: String?,
        name: String?,
        descriptor: String?,
        ownerInternalName: String?
    ) {
        escape(name)
    }

    override fun indent() {
        indentationCount++
    }

    override fun unindent() {
        if (indentationCount > 0) indentationCount--
    }

    override fun startLine(lineNumber: Int) {
        for (i in 0 until indentationCount) stringBuffer.append(TAB)
    }

    override fun endLine() {
        stringBuffer.append(NEWLINE)
    }

    override fun extraLine(count: Int) {
        var count = count
        if (realignmentLineNumber) while (count-- > 0) stringBuffer.append(NEWLINE)
    }

    override fun startMarker(type: Int) {}
    override fun endMarker(type: Int) {}

    companion object {
        protected const val TAB: String = "  "
        protected const val NEWLINE: String = "\n"
    }
}