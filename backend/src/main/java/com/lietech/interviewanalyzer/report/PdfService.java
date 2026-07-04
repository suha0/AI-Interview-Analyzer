package com.lietech.interviewanalyzer.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.lietech.interviewanalyzer.interview.Interview;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateInterviewReport(
            Interview interview,
            String feedback
    ) {

        try {

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            Document document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    out
            );

            document.open();

            document.add(
                    new Paragraph(
                            "AI Interview Analysis Report"
                    )
            );

            document.add(new Paragraph(" "));

            document.add(
                    new Paragraph(
                            "Interview: "
                                    + interview.getTitle()
                    )
            );

            document.add(
                    new Paragraph(
                            "Target Role: "
                                    + interview.getTargetRole()
                    )
            );

            document.add(
                    new Paragraph(
                            "Score: "
                                    + interview.getTotalScore()
                    )
            );

            document.add(
                    new Paragraph(
                            "Status: "
                                    + interview.getStatus()
                    )
            );

            document.add(
                    new Paragraph(
                            "Completed At: "
                                    + interview.getCompletedAt()
                    )
            );

            document.add(new Paragraph(" "));
            document.add(
                    new Paragraph(
                            "AI Feedback"
                    )
            );

            document.add(
                    new Paragraph(feedback)
            );

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate PDF",
                    e
            );
        }
    }
}