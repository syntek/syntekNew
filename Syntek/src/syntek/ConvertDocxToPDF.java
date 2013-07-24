/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syntek;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.docx4j.convert.out.pdf.viaXSLFO.Conversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 *
 * @author MrOnly
 */
public class ConvertDocxToPDF {

    public ConvertDocxToPDF() {
    }

    public void convert(String inputPath, String outputPath) {
        PropertyConfigurator.configure("log4j.properties");
        try {
            WordprocessingMLPackage mlp = WordprocessingMLPackage.load(new FileInputStream(inputPath));
            Mapper mapper = new IdentityPlusMapper();
            PhysicalFonts.discoverPhysicalFonts();
            Map<String, PhysicalFont> mapfont = PhysicalFonts.getPhysicalFonts();
            mapper.getFontMappings().putAll(mapfont);
            Conversion myConversion = new Conversion(mlp);
            DateFormat dateFormat = new SimpleDateFormat("HH_mm_ss");
            //get current date time with Date()
            Date date = new Date();
            String temp=dateFormat.format(date);
            long current = System.currentTimeMillis();
            String myfile = inputPath.substring((inputPath.lastIndexOf("\\")+1));
            System.out.println(myfile);
            OutputStream os = new FileOutputStream(outputPath+"\\"+myfile+"_"+String.valueOf(current)+".pdf");
            myConversion.output(os, new PdfSettings());
        } catch (Exception ex) {
            Logger.getLogger(ConvertDocxToPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
