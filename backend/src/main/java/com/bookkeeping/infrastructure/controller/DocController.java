package com.bookkeeping.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.MediaType;

/**
 * Serve Swagger UI at /doc.html endpoint.
 * This provides a more user-friendly URL for API documentation.
 */
@Controller
public class DocController {

    @GetMapping(value = "/doc.html", produces = MediaType.TEXT_HTML_VALUE)
    public RedirectView swaggerUi() {
        // Redirect to the static HTML page which loads Swagger UI
        return new RedirectView("/doc/index.html", true);
    }
}