package com.aps.Visitors_form.controller;

import com.aps.Visitors_form.entity.Visitor;
import com.aps.Visitors_form.repository.VisitorRepository;
import com.aps.Visitors_form.utils.Base64ImageEncoder;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class VisitorController {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private Base64ImageEncoder imageEncoder;

    @GetMapping("/visitorForm")
    public String showForm(Model model) {
        model.addAttribute("visitor", new Visitor());
        return "visitorForm";
    }

    @PostMapping("/submitForm")
    public String submitForm(
            @RequestParam("name") String name,
            @RequestParam("role") String role,
            @RequestParam("purpose") String purpose,
            @RequestParam("photo") String photo, // Capture photo as base64 string
            RedirectAttributes redirectAttributes,
            @Valid Visitor visitor, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            // Return to the form with error messages
            return "visitorForm";
        }

        // Decode base64 string to byte array
        byte[] photoBytes = Base64.getDecoder().decode(photo.getBytes(StandardCharsets.UTF_8));

        // Create a new Visitor instance and save it to the database
        Visitor visitorr = new Visitor(name, role, purpose, LocalDateTime.now(), photoBytes);
        visitorRepository.save(visitorr);

        // Add data to redirect attributes
        redirectAttributes.addFlashAttribute("name", name);
        redirectAttributes.addFlashAttribute("role", role);
        redirectAttributes.addFlashAttribute("purpose", purpose);
        redirectAttributes.addFlashAttribute("visitDateTime", visitorr.getVisitDateTime());

        // Redirect to formSuccess
        return "redirect:/formSuccess";
    }

    @GetMapping("/formSuccess")
    public String formSuccess() {
        return "formSuccess";  // Renders formSuccess.html
    }

    // Show admin login page
    @GetMapping("/adminLogin")
    public String showAdminLogin() {
        return "adminLogin";
    }

    // Handle admin login
    @PostMapping("/adminLogin")
    public String adminLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Simple hardcoded check for username and password
        if ("admin".equals(username) && "password123".equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/allVisitors";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/adminLogin";
        }
    }

    // Show all visitors (accessible only to admin)
    @GetMapping("/allVisitors")
    public String showAllVisitors(Model model, HttpSession session,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size) {

        // Check if admin is logged in
        Boolean isLoggedIn = (Boolean) session.getAttribute("adminLoggedIn");
        if (isLoggedIn == null || !isLoggedIn) {
            return "redirect:/adminLogin";
        }

        // Fetch paginated data
        Page<Visitor> visitorsPage = visitorRepository.findAll(PageRequest.of(page, size));

//        // Add pagination attributes to the model
//        model.addAttribute("visitorsPage", visitorsPage);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", visitorsPage.getTotalPages());
//        model.addAttribute("pageSize", size);

        // Encode images to Base64 and set them as additional attributes
        model.addAttribute("visitors", visitorsPage.getContent().stream()
                .map(visitor -> {
                    String photoBase64 = (visitor.getPhoto() != null) ? imageEncoder.encodeImage(visitor.getPhoto()) : null;
                    visitor.setPhotoBase64(photoBase64);
                    return visitor;
                })
                .collect(Collectors.toList()));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", visitorsPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "allVisitors";
    }

    // Admin logout
    @GetMapping("/adminLogout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/visitorForm";
    }

}

