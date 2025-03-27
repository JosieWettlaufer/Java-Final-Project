package com.example.GridFSDemo.controller;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller  // Changed from @RestController to @Controller for form-based views
@RequestMapping("/media")
public class FileUploadController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    // Show upload form
    @GetMapping("/uploadform")
    public String showUploadForm() {
        return "uploadform";  // This will render a Thymeleaf template
    }

    // Upload file
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) throws IOException {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Store file with user metadata
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                Document.parse("{\"username\": \"" + username + "\"}") // Store user who uploaded
        );

        redirectAttributes.addFlashAttribute("message",
                "File uploaded successfully! ID: " + id.toHexString());
        return "redirect:/media/list";  // Redirect to file list page
    }

    // List user's files
    @GetMapping("/list")
    public String listFiles(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Find files uploaded by current user
        Query query = new Query(where("metadata.username").is(username));
        List<GridFSFile> files = new ArrayList<>();
        gridFsTemplate.find(query).forEach(files::add);

        model.addAttribute("files", files);
        return "filelist";  // Render file list template
    }

    // Download file
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable String id) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id))));

        if (gridFSFile == null) {
            return ResponseEntity.notFound().build();
        }

        // Optional: Check if current user has permission to access this file
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String fileOwner = gridFSFile.getMetadata().get("username").toString();

        if (!username.equals(fileOwner) && !auth.getAuthorities().contains("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(gridFSFile.getMetadata().get("_contentType").toString()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + gridFSFile.getFilename())
                .body(new InputStreamResource(gridFsOperations.getResource(gridFSFile).getInputStream()));
    }
}