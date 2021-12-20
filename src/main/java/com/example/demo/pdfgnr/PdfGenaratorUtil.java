package com.example.demo.pdfgnr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
@Component
public class PdfGenaratorUtil {
	@Autowired

    private TemplateEngine templateEngine;

    public String createPdf(String templatename, Map map) throws Exception  {

     String fileNameUrl = "";

     Context ctx = new Context();

     if (map != null) {

          Iterator itMap = map.entrySet().iterator();

            while (itMap.hasNext()) {

        Map.Entry pair = (Map.Entry) itMap.next();

               ctx.setVariable(pair.getKey().toString(), pair.getValue());

      }

     }     

     String processedHtml = templateEngine.process(templatename, ctx);

       FileOutputStream os = null;

       String studentId = map.get("ID").toString();

            try {

                final File outputFile = File.createTempFile("Student_"+studentId+"_", ".pdf");

                os = new FileOutputStream(outputFile);

                ITextRenderer itr = new ITextRenderer();

                itr.setDocumentFromString(processedHtml);

                itr.layout();

                itr.createPDF(os, false);

                itr.finishPDF();

                fileNameUrl = outputFile.getName();

            }

            finally {

                if (os != null) {

                    try {

                        os.close();

                    } catch (IOException e) { }

                }

            }

            return fileNameUrl;
            
            
            

    }

}
