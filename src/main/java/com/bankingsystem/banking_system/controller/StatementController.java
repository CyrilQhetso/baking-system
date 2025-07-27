package com.bankingsystem.banking_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bankingsystem.banking_system.service.StatementService;

import java.time.YearMonth;

@RestController
@RequestMapping("/statements")
@CrossOrigin(origins = {"http://localhost:4200", "https://baking-system-frontend.vercel.app"})
public class StatementController {
    @Autowired
    private StatementService statementService;
    
    @GetMapping("/monthly")
    public ResponseEntity<byte[]> generateMonthlyStatement(
            @RequestParam String accountId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        
        try {
            // Validate input parameters
            if (year == null || month == null || year < 2000 || year > 2100 || month < 1 || month > 12) {
                return ResponseEntity.badRequest().build();
            }
            
            // Check if the requested month is not in the future
            YearMonth requestedMonth = YearMonth.of(year, month);
            YearMonth currentMonth = YearMonth.now();
            
            if (requestedMonth.isAfter(currentMonth)) {
                return ResponseEntity.badRequest().build();
            }
            
            // Generate statement
            byte[] pdfBytes = statementService.generateMonthlyStatement(accountId, year, month, auth.getName());
            
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("statement-%d-%02d.pdf", year, month));
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
