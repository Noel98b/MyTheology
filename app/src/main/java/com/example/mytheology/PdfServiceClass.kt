package com.example.mytheology

import android.provider.FontRequest
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*

class PdfServiceClass {

    fun createPDFFile(path: String) {
        if(File(path).exists())
            File(path).delete()
        try{
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(path))
            document.open()
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("email from login")
            document.addCreator("fname lname")

            val colorAccent = BaseColor(0,153,204,255)
            val titleFontSize = 20.0f
            val valueFontSize = 26.0f

            val fontName = BaseFont.createFont("assets/fonts/eastman_regular.otf", "UTF-8", BaseFont.EMBEDDED)

            var titleStyle = Font(fontName, 36.0f, Font.NORMAL,BaseColor.BLACK)
            addNewItem(document, "Details", Element.ALIGN_CENTER, titleStyle)

            val headingStyle = Font(fontName, titleFontSize, Font.NORMAL, colorAccent)
            addNewItem(document, "Nummer", Element.ALIGN_LEFT, headingStyle)
            val valueStyle = Font(fontName, titleFontSize, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "123123", Element.ALIGN_LEFT, valueStyle)

            addLineSeperator(document)

            addNewItem(document, "Date", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "25.02.2021", Element.ALIGN_LEFT, valueStyle)

            addLineSeperator(document)

            addNewItem(document, "Name", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "Otto", Element.ALIGN_LEFT, valueStyle)

            addLineSeperator(document)
            addNewItem(document, "More Details", Element.ALIGN_CENTER, titleStyle)

            addLineSeperator(document)
            addNewItemWithLeftAndRight(document, "Pizza 25", "(0,0%)", titleStyle, valueStyle)

        }catch (e: Exception){

        }
    }

    @Throws(DocumentException::class)
    private fun addNewItemWithLeftAndRight(document: Document, left: String, right: String, leftStyle: Font, rightStyle: Font) {
        val chunkTextLeft = Chunk(left, leftStyle)
        val chunkTextRight = Chunk(right, rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }

    @Throws(DocumentException::class)
    private fun addLineSeperator(document: Document) {
        val lineseperator = LineSeparator()
        lineseperator.lineColor = BaseColor(0,0,0,69)
        addLineSpace(document)
        document.add(Chunk(lineseperator))
        addLineSpace(document)
    }

    @Throws(DocumentException::class)
    private fun addLineSpace(document: Document) {
        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, align: Int, style: Font) {
            val chunk = Chunk(text, style)
            val p = Paragraph(chunk)
            p.alignment = align
            document.add(p)
    }
}