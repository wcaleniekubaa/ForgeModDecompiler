package com.github.wcaleniekubaa.fmd.ui

import com.github.wcaleniekubaa.fmd.Decompiler
import com.github.wcaleniekubaa.fmd.FMDMain
import java.awt.Dimension
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JTextField
import javax.swing.filechooser.FileFilter

class FMDUI : JFrame("ForgeModDecompiler") {
    init {
        size = Dimension(450, 150)
        isVisible = true
        defaultCloseOperation = 3
        isResizable = false
        addKeyListener(KeyHandler())


        val input = JTextField()
        val startBtn = JButton("Start")
        val browseBtn = JButton("Browse")
        val comboBox = JComboBox<File>()
        val decompilerComboBox = JComboBox<Decompiler>()

        startBtn.size = Dimension(100, 35)
        startBtn.location = Point(460 - 130, 75)
        add(startBtn)
        startBtn.addActionListener {
            Thread({
                FMDMain.process(comboBox.getItemAt(comboBox.selectedIndex), File(input.text), decompilerComboBox.getItemAt(decompilerComboBox.selectedIndex))
            }, "FMD-Thread") .start()
        }

        input.size = Dimension(320, 30)
        input.location = Point(10, 10)
        add(input)

        browseBtn.size = Dimension(100, 30)
        browseBtn.location = Point(460 - 130, 10)
        add(browseBtn)

        browseBtn.addActionListener {
            val browser = JFileChooser()
            if(browser.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION) {
                input.text = browser.selectedFile.absolutePath
            }
        }


        for(file in FMDMain.mappingsFiles) {
            comboBox.addItem(file)
        }
        comboBox.size = Dimension(320, 35)
        comboBox.location = Point(10, 40)
        for(decompiler in Decompiler.values()) {
            decompilerComboBox.addItem(decompiler)
        }
        decompilerComboBox.size = Dimension(320, 35)
        decompilerComboBox.location = Point(10, 75)
        add(comboBox)
        add(decompilerComboBox)




        repaint()
        layout = null
    }
}

class KeyHandler : KeyListener {

    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent) {
        val isRepaintKeyPressed = e.keyCode == KeyEvent.VK_F5
        if (isRepaintKeyPressed) {
            if (e.source is JFrame) {
                (e.source as JFrame).repaint()
                FMDMain.logger.debug("Successfully repainted JFrame.")
            } else {
                FMDMain.logger.debug(e.source.javaClass.name)
            }
        }

    }

    override fun keyReleased(e: KeyEvent?) {

    }

}