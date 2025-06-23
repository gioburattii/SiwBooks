package siwbooks.siwbooks.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);
            
            switch (statusCode) {
                case 404:
                    model.addAttribute("error", "Pagina non trovata");
                    model.addAttribute("message", "La pagina richiesta non esiste.");
                    break;
                case 403:
                    model.addAttribute("error", "Accesso negato");
                    model.addAttribute("message", "Non hai i permessi per accedere a questa risorsa.");
                    break;
                case 500:
                    model.addAttribute("error", "Errore interno del server");
                    model.addAttribute("message", "Si è verificato un errore interno. Riprova più tardi.");
                    break;
                default:
                    model.addAttribute("error", "Errore");
                    model.addAttribute("message", "Si è verificato un errore imprevisto.");
                    break;
            }
        }

        // Aggiungi dettagli per il debug (solo in sviluppo)
        if (exception != null) {
            model.addAttribute("exception", exception.toString());
        }
        if (message != null) {
            model.addAttribute("errorMessage", message.toString());
        }
        if (requestUri != null) {
            model.addAttribute("requestUri", requestUri.toString());
        }

        return "error";
    }
} 