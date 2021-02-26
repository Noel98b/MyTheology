package com.example.mytheology

import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import java.io.File
import java.io.FileOutputStream

class PdfServiceClass {

    fun createPDFFile(path: String, data: MainModel) {

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

            val colorAccent = BaseColor(0, 153, 204, 255)
            val valueFontSize = 12.0f
            val titleFontSize = 36.0f
            val headingfontsize = 16.0f

            val fontName = BaseFont.createFont("assets/fonts/Gravity-Regular.otf", "UTF-8", BaseFont.EMBEDDED)

            var titleStyle = Font(fontName, titleFontSize, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, data.sectionTitle.toString(), Element.ALIGN_CENTER, titleStyle)
            val headingStyle = Font(fontName, headingfontsize, Font.NORMAL, colorAccent)
            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)

            for (j in 0 until (data.entries?.size!!)){
                val result = StringBuilder()
                var resetIndex = 0
                var lastSpaceIndex = 0
                for (i in 0 until (data.entries!![j].entry?.length!!)) {
                    if(data.entries!![j].entry?.get(i)=='\n'){
                        result.append(' ')
                    }else{
                        result.append(data.entries!![j].entry?.get(i))
                    }
                    if (result[i] ==' '){
                        lastSpaceIndex = i
                    }

                    if(resetIndex>35){
                        result[lastSpaceIndex] = '\n'
                        resetIndex = 0
                    }
                    resetIndex = resetIndex +1
                }
                data.entries!![j].entry = result.toString()
            }


                     for(i in 0 until data.entries?.size!!-1){


                         if ( (i % 2 == 0) ){
                             addLineSpace(document)
                             addLineSeperator(document)
                             addLineSpace(document)
                             val chunkTitleLeft =  Chunk(data.entries!![i].title, headingStyle)
                             val chunkTextLeft =  Chunk(data.entries!![i].entry, valueStyle)
                             val chunkTitleRight =  Chunk(data.entries!![i + 1].title, headingStyle)
                             val chunkTextRight =  Chunk(data.entries!![i + 1].entry, valueStyle)

                             val pH = Paragraph(chunkTitleLeft)
                             pH.add("\n")
                             pH.add(chunkTextLeft)
                             pH.alignment = Element.ALIGN_LEFT

                             val pR = Paragraph(chunkTitleRight)
                             pR.add("\n")
                             pR.add(chunkTextRight)
                             pR.alignment = Element.ALIGN_RIGHT

                             document.add(pH)
                             addLineSpace(document)
                             addLineSeperator(document)
                             addLineSpace(document)
                             document.add(pR)

                         }
                     }

            if (data.entries?.size!! % 2 != 0){
                addLineSpace(document)
                addLineSeperator(document)
                addLineSpace(document)
                val chunkTitleLeft =  Chunk(data.entries!!.last().title, headingStyle)
                val chunkTextLeft =  Chunk(data.entries!!.last().entry, valueStyle)

                val p = Paragraph(chunkTitleLeft)
                p.add("\n")
                p.add(chunkTextLeft)
                document.add(p)
            }

            document.close()


        }catch (e: Exception){
            Log.e("failure", "" + e.message)

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
        lineseperator.lineColor = BaseColor(0, 0, 0, 69)
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

