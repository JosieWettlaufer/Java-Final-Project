package com.example.GridFSDemo.controller;

import com.example.GridFSDemo.model.TimeCapsule;
import com.example.GridFSDemo.repository.TimeCapsuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller // Changed from @RestController to @Controller
@RequestMapping("/capsules")
public class TimeCapsuleController {

    @Autowired
    private TimeCapsuleRepository timeCapsuleRepository;

    // Show form to create a new capsule
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("capsule", new TimeCapsule());
        return "capsuleCreate";
    }

    // Create a new time capsule
    @PostMapping("/create")
    public String createCapsule(@ModelAttribute TimeCapsule capsule,
                                RedirectAttributes redirectAttributes) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // Using username as userId for simplicity

        capsule.setUserId(userId);
        capsule.setCreatedAt(LocalDate.now());
        capsule.setUnlocked(LocalDate.now().isAfter(capsule.getLockDate()));

        timeCapsuleRepository.save(capsule);

        redirectAttributes.addFlashAttribute("message", "Time capsule created successfully!");
        return "redirect:/capsules/myCapsules";
    }

    // List user's capsules
    @GetMapping("/myCapsules")
    public String getMyCapsules(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        List<TimeCapsule> capsules = timeCapsuleRepository.findByUserId(userId);
        model.addAttribute("capsules", capsules);

        return "capsuleList";
    }

    // Show single capsule details
    @GetMapping("/{id}")
    public String getCapsuleById(@PathVariable String id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        TimeCapsule capsule = timeCapsuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capsule not found with ID: " + id));

        // Check if user owns this capsule or is an admin
        if (!capsule.getUserId().equals(userId) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "accessDenied";
        }

        model.addAttribute("capsule", capsule);
        return "capsuleDetail";
    }

    // Confirm delete capsule
    @GetMapping("/{id}/delete")
    public String confirmDeleteCapsule(@PathVariable String id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        TimeCapsule capsule = timeCapsuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capsule not found with ID: " + id));

        // Check if user owns this capsule
        if (!capsule.getUserId().equals(userId) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "accessDenied";
        }

        model.addAttribute("capsule", capsule);
        return "capsuleDeleteConfirm";
    }

    // Delete capsule by ID
    @PostMapping("/{id}/delete")
    public String deleteCapsule(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        TimeCapsule capsule = timeCapsuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capsule not found with ID: " + id));

        // Check if user owns this capsule
        if (!capsule.getUserId().equals(userId) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "accessDenied";
        }

        timeCapsuleRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Time capsule deleted successfully!");
        return "redirect:/capsules/myCapsules";
    }
}