package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.example.demo.model.Student;
import com.example.demo.pdfgnr.PdfGenaratorUtil;
import com.example.demo.repository.StudentRepository;
import com.lowagie.text.pdf.codec.Base64.OutputStream;

@RestController
public class StudentController {
	@Autowired

	private StudentRepository studentRepository;

	@Autowired

	private PdfGenaratorUtil pdfGenaratorUtil;
	
	@Autowired
	private TemplateEngine templateEngine;

	@GetMapping("student/{studentId}")

	public ResponseEntity getStudentInfoPdf(@PathVariable Integer studentId) throws Exception {

		Student student = studentRepository.findById(studentId).orElse(null);

		if (student == null)

			throw new Exception("Student not present");

		Map<String, Object> studentMap = new HashMap<>();

		studentMap.put("ID", student.getId());

		studentMap.put("firstName", student.getFirstName());

		studentMap.put("lastName", student.getLastName());

		studentMap.put("roomname", student.getRoomname());
		generatePdf(studentMap);

		org.springframework.core.io.Resource resource = null;

		try {

			String property = "java.io.tmpdir";

			String tempDir = System.getProperty(property);

			String fileNameUrl = pdfGenaratorUtil.createPdf("Student", studentMap);

			Path path = Paths.get(tempDir + "/" + fileNameUrl);

			resource = new UrlResource(path.toUri());

		} catch (Exception e) {

			e.printStackTrace();

		}

		return ResponseEntity.ok()

				.contentType(org.springframework.http.MediaType
						.parseMediaType(org.springframework.http.MediaType.APPLICATION_PDF_VALUE))

				.header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + resource.getFilename() + "\"")

				.body(resource);

	}
	
	@GetMapping("/generatepdf/{studentId}")
	public void generatePdf(Map<String, Object> studentMap2) throws Exception {
		 TemplateEngine templateEngine = new TemplateEngine();
		 Context context = new Context();
		// Map<String, Object> studentMap = new HashMap<>();
	        context.setVariable("data",studentMap2);
	        String renderedHtmlContent = templateEngine.process("template", context);
	        String xHtml = convertToXhtml(renderedHtmlContent);

	        ITextRenderer renderer = new ITextRenderer();
	       // renderer.getFontResolver().addFont("Code39.ttf", IDENTITY_H, EMBEDDED);
	        String baseUrl = FileSystems
	                                .getDefault()
	                                .getPath("src", "test", "resources")
	                                .toUri()
	                                .toURL()
	                                .toString();
	        renderer.setDocumentFromString(xHtml, baseUrl);
	        renderer.layout();

	        FileOutputStream outputStream = new FileOutputStream(new File("src/main/resources/pdf/message.pdf"));
	        renderer.createPDF(outputStream);
	        outputStream.close();
	    }
	
private String convertToXhtml(String html) throws UnsupportedEncodingException {
    Tidy tidy = new Tidy();
    tidy.setInputEncoding("UTF_8");
    tidy.setOutputEncoding("UTF_8");
    tidy.setXHTML(true);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    tidy.parseDOM(inputStream, outputStream);
    return outputStream.toString(StandardCharsets.UTF_8);
}


}
