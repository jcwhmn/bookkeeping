package com.bookkeeping.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.MediaType;

/**
 * Redirect API documentation to Scalar UI.
 * Access at /doc.html → redirects to /scalar
 */
@Controller
public class DocController {

    @GetMapping(value = "/doc.html", produces = MediaType.TEXT_HTML_VALUE)
    public RedirectView docRedirect() {
        return new RedirectView("/scalar", true);
    }

    @GetMapping(value = "/swagger-ui.html", produces = MediaType.TEXT_HTML_VALUE)
    public RedirectView swaggerUiRedirect() {
        return new RedirectView("/scalar", true);
    }
}
