package com.polling.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.Random;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_EXPIRE_MINUTES = 5;

    public String generateAndStoreOtp(HttpSession session, String identifier) {
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        
        session.setAttribute("OTP_CODE", otp);
        session.setAttribute("OTP_TIME", System.currentTimeMillis());
        session.setAttribute("OTP_IDENTIFIER", identifier);

        logger.info("=========================================");
        logger.info("SECURITY OTP GENERATED FOR [{}]: {}", identifier, otp);
        logger.info("=========================================");

        return otp;
    }

    public boolean validateOtp(HttpSession session, String enteredOtp) {
        String storedOtp = (String) session.getAttribute("OTP_CODE");
        Long otpTime = (Long) session.getAttribute("OTP_TIME");

        if (storedOtp == null || otpTime == null || enteredOtp == null) {
            return false;
        }

        // Check expiration (5 minutes = 300,000 ms)
        if (System.currentTimeMillis() - otpTime > (OTP_EXPIRE_MINUTES * 60 * 1000)) {
            clearOtp(session);
            return false;
        }

        boolean isValid = storedOtp.trim().equals(enteredOtp.trim());

        if (isValid) {
            clearOtp(session);
        }

        return isValid;
    }

    public String getCurrentOtp(HttpSession session) {
        return (String) session.getAttribute("OTP_CODE");
    }

    public void clearOtp(HttpSession session) {
        session.removeAttribute("OTP_CODE");
        session.removeAttribute("OTP_TIME");
        session.removeAttribute("OTP_IDENTIFIER");
    }
}
