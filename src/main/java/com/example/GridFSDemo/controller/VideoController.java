package com.example.GridFSDemo.controller;

import com.example.GridFSDemo.model.Video;
import com.example.GridFSDemo.service.VideoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//Controller class for handling video uploads, retrieval, and streaming
@Controller
public class VideoController {

    //Injects VideoService to handle video-related logic
    @Autowired
    private VideoService videoService;

    /**
     * Displays the video upload page.
     * If a title is provided as a request parameter, it is added to the model.
     *
     * @param title Optional title parameter
     * @param model Model to pass data to the view
     * @return View name "uploadVideo"
     */
    @GetMapping("/videos")
    public String uploadPhoto(@RequestParam(value = "title", required = false) String title, Model model) {
        model.addAttribute("title", title);
        return "uploadVideo";
    }

    /**
     * Handles video upload.
     * Saves the video using the VideoService and redirects to the video's details page.
     *
     * @param title Title of the video
     * @param file  Video file uploaded by the user
     * @param model Model to pass data to the view
     * @return Redirect URL to view the uploaded video
     * @throws IOException If there is an error reading the file
     */
    @PostMapping("/videos/add")
    public String addVideo(@RequestParam("title") String title,
                           @RequestParam("file") MultipartFile file, Model model) throws IOException {
        String id = videoService.addVideo(title, file); //Save video and get its generated ID
        return "redirect:/videos/" + id;    //Redirect to video details page based on generated ID
    }

    /**
     * Retrieves a video by its ID and prepares it for display.
     * The video title and its streaming URL are added to the model.
     *
     * @param id    ID of the video
     * @param model Model to pass data to the view
     * @return View name "videos"
     * @throws Exception If the video is not found or an error occurs
     */
    @GetMapping("/videos/{id}")
    public String getVideo(@PathVariable String id, Model model) throws Exception {
        Video video = videoService.getVideo(id); //Fetch video details from db
        model.addAttribute("title", video.getTitle()); //Add video title to modedl
        model.addAttribute("url", "/videos/stream/" + id); //URL for streaming video
        return "videos";
    }

    /**
     * Streams a video directly to the client's browser.
     * This method fetches the video stream and writes it to the response output stream.
     *
     * @param id       ID of the video to stream
     * @param response HTTP response to write the video stream
     * @throws Exception If the video is not found or an error occurs while streaming
     */
    @GetMapping("/videos/stream/{id}")
    public void streamVideo(@PathVariable String id, HttpServletResponse response) throws Exception {
        Video video = videoService.getVideo(id);
        FileCopyUtils.copy(video.getStream(), response.getOutputStream()); // Stream the video to the client
    }
}
