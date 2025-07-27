package com.bankingsystem.banking_system.service;

import com.bankingsystem.banking_system.dto.StatementData;
import com.bankingsystem.banking_system.entity.Transaction;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.Month;

@Service
public class PdfGenerationService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = 595;
    private static final float PAGE_HEIGHT = 842;
    private static final float LINE_HEIGHT = 15;
    
    public byte[] generateStatementPdf(StatementData statementData) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            float yPosition = PAGE_HEIGHT - MARGIN;
            
            // Header
            yPosition = addHeader(contentStream, yPosition);
            
            // Account Information
            yPosition = addAccountInformation(contentStream, statementData, yPosition);
            
            // Statement Period
            yPosition = addStatementPeriod(contentStream, statementData, yPosition);
            
            // Balance Summary
            yPosition = addBalanceSummary(contentStream, statementData, yPosition);
            
            // Transaction History
            yPosition = addTransactionHistory(contentStream, statementData, yPosition, document);
            
            // Footer
            addFooter(contentStream);
            
            contentStream.close();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF statement", e);
        }
    }
    
    private float addHeader(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
        contentStream.newLineAtOffset(PAGE_WIDTH / 2 - 70, yPosition);
        contentStream.showText("DIGITAL BANK");
        contentStream.endText();
        
        yPosition -= 30;
        
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
        contentStream.newLineAtOffset(PAGE_WIDTH / 2 - 110, yPosition);
        contentStream.showText("MONTHLY ACCOUNT STATEMENT");
        contentStream.endText();
        
        return yPosition - 40;
    }
    
    private float addAccountInformation(PDPageContentStream contentStream, StatementData data, float yPosition) throws IOException {
        yPosition = addSectionHeader(contentStream, "ACCOUNT INFORMATION", yPosition);
        
        yPosition = addKeyValuePair(contentStream, "Account Holder:", data.getUser().getName(), yPosition);
        yPosition = addKeyValuePair(contentStream, "Account Number:", data.getAccount().getAccountNumber(), yPosition);
        yPosition = addKeyValuePair(contentStream, "Account Type:", data.getAccount().getAccountType().toUpperCase(), yPosition);
        yPosition = addKeyValuePair(contentStream, "Email:", data.getUser().getEmail(), yPosition);
        
        return yPosition - 10;
    }
    
    private float addStatementPeriod(PDPageContentStream contentStream, StatementData data, float yPosition) throws IOException {
        yPosition = addSectionHeader(contentStream, "STATEMENT PERIOD", yPosition);
        
        String monthName = Month.of(data.getMonth()).name();
        String period = String.format("%s %d", monthName, data.getYear());
        
        yPosition = addKeyValuePair(contentStream, "Statement Period:", period, yPosition);
        yPosition = addKeyValuePair(contentStream, "From:", data.getStatementPeriodStart().format(DATE_ONLY_FORMATTER), yPosition);
        yPosition = addKeyValuePair(contentStream, "To:", data.getStatementPeriodEnd().format(DATE_ONLY_FORMATTER), yPosition);
        
        return yPosition - 10;
    }
    
    private float addBalanceSummary(PDPageContentStream contentStream, StatementData data, float yPosition) throws IOException {
        yPosition = addSectionHeader(contentStream, "BALANCE SUMMARY", yPosition);
        
        yPosition = addKeyValuePair(contentStream, "Opening Balance:", "R " + data.getOpeningBalance().toString(), yPosition);
        yPosition = addKeyValuePair(contentStream, "Closing Balance:", "R " + data.getClosingBalance().toString(), yPosition);
        
        return yPosition - 10;
    }
    
    private float addTransactionHistory(PDPageContentStream contentStream, StatementData data, float yPosition, PDDocument document) throws IOException {
        yPosition = addSectionHeader(contentStream, "TRANSACTION HISTORY", yPosition);
        
        if (data.getTransactions().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            contentStream.newLineAtOffset(PAGE_WIDTH / 2 - 80, yPosition);
            contentStream.showText("No transactions found for this period.");
            contentStream.endText();
            return yPosition - 20;
        }
        
        // Table headers
        yPosition = addTransactionTableHeader(contentStream, yPosition);
        
        // Sort transactions by timestamp
        data.getTransactions().sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
        
        BigDecimal runningBalance = data.getOpeningBalance();
        
        for (Transaction transaction : data.getTransactions()) {
            // Check if we need a new page
            if (yPosition < 100) {
                contentStream.close();
                PDPage newPage = new PDPage();
                document.addPage(newPage);
                contentStream = new PDPageContentStream(document, newPage);
                yPosition = PAGE_HEIGHT - MARGIN;
                yPosition = addTransactionTableHeader(contentStream, yPosition);
            }
            
            // Update running balance
            if (isCredit(transaction, data.getAccount().getId())) {
                runningBalance = runningBalance.add(transaction.getAmount());
            } else {
                runningBalance = runningBalance.subtract(transaction.getAmount());
            }
            
            yPosition = addTransactionRow(contentStream, transaction, runningBalance, data.getAccount().getId(), yPosition);
        }
        
        return yPosition;
    }
    
    private float addTransactionTableHeader(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Date");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("Description");
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText("Type");
        contentStream.newLineAtOffset(80, 0);
        contentStream.showText("Amount");
        contentStream.newLineAtOffset(80, 0);
        contentStream.showText("Balance");
        contentStream.endText();
        
        // Draw line under headers
        contentStream.moveTo(MARGIN, yPosition - 5);
        contentStream.lineTo(PAGE_WIDTH - MARGIN, yPosition - 5);
        contentStream.stroke();
        
        return yPosition - 20;
    }
    
    private float addTransactionRow(PDPageContentStream contentStream, Transaction transaction, 
                                   BigDecimal runningBalance, String accountId, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(transaction.getTimestamp().format(DATE_FORMATTER));
        contentStream.newLineAtOffset(100, 0);
        
        String description = getTransactionDescription(transaction);
        if (description.length() > 20) {
            description = description.substring(0, 17) + "...";
        }
        contentStream.showText(description);
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText(transaction.getType().toString());
        contentStream.newLineAtOffset(80, 0);
        contentStream.showText(formatAmount(transaction, accountId));
        contentStream.newLineAtOffset(80, 0);
        contentStream.showText("R " + runningBalance.toString());
        contentStream.endText();
        
        return yPosition - LINE_HEIGHT;
    }
    
    private float addSectionHeader(PDPageContentStream contentStream, String title, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        
        return yPosition - 20;
    }
    
    private float addKeyValuePair(PDPageContentStream contentStream, String key, String value, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(key);
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
        contentStream.newLineAtOffset(150, 0);
        contentStream.showText(value);
        contentStream.endText();
        
        return yPosition - LINE_HEIGHT;
    }
    
    private void addFooter(PDPageContentStream contentStream) throws IOException {
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
        contentStream.newLineAtOffset(MARGIN, 60);
        contentStream.showText("This statement is computer generated and does not require a signature.");
        contentStream.newLineAtOffset(0, -10);
        contentStream.showText("For any queries, please contact customer service.");
        contentStream.endText();
    }
    
    private boolean isCredit(Transaction transaction, String accountId) {
        return accountId.equals(transaction.getToAccountId());
    }
    
    private String formatAmount(Transaction transaction, String accountId) {
        String prefix = isCredit(transaction, accountId) ? "+" : "-";
        return prefix + "R " + transaction.getAmount().toString();
    }
    
    private String getTransactionDescription(Transaction transaction) {
        if (transaction.getDescription() != null && !transaction.getDescription().trim().isEmpty()) {
            return transaction.getDescription();
        }
        
        switch (transaction.getType()) {
            case DEPOSIT:
                return "Deposit";
            case WITHDRAWAL:
                return "Withdrawal";
            case TRANSFER:
                return "Transfer";
            default:
                return "Transaction";
        }
    }
}
