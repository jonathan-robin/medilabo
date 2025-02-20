package com.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomForbiddenException.class)
    public String handleForbiddenException(CustomForbiddenException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Votre session a expiré ou vous n'avez pas les permissions.");
        return "redirect:unauthorized"; // Redirection vers la page d'erreur
    }
}