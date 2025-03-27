package com.example.GridFSDemo.controller;

import com.example.GridFSDemo.model.Photo;
import com.example.GridFSDemo.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;


//Controller for photo-related operations
@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;


    /**
     * Displays the photo upload page.
     * @param title Optional title for the photo.
     * @param model Spring Model to pass data to the view.
     * @return The upload photo view template.
     */
    @GetMapping("/photos")
    public String uploadPhoto(@RequestParam(value = "title", required = false) String title, Model model) {
        model.addAttribute("title", title);
        return "uploadPhoto";
    }

    /**
     * Handles photo upload and redirects to the uploaded photo's page.
     * @param title The title of the uploaded photo.
     * @param image The uploaded image file.
     * @param model Spring Model for passing data.
     * @return Redirects to the specific photo's view page.
     * @throws IOException If an error occurs during file upload.
     */
    @PostMapping("/photos/add")
    public String addPhoto(@RequestParam("title") String title,
                           @RequestParam("image") MultipartFile image, Model model)
            throws IOException {
        String id = photoService.addPhoto(title, image); //Call service to save photo
        return "redirect:/photos/" + id; //Redirect to photo's page using id
    }


    /**
     * Retrieves and displays a photo by ID.
     * @param id The ID of the photo.
     * @param model Spring Model to pass data to the view.
     * @return The photo display view template.
     */
    @GetMapping("/photos/{id}")
    public String getPhoto(@PathVariable String id, Model model) {
        Photo photo = photoService.getPhoto(id); //Fetch photo from service
        model.addAttribute("title", photo.getTitle());  //set title in model
        model.addAttribute("image",
                Base64.getEncoder().encodeToString(photo.getImage().getData())); //convert image binary data from db to Base64
        return "photos";
    }
}
