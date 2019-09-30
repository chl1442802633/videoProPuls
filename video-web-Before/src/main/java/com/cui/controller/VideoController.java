package com.cui.controller;

import com.cui.pojo.*;
import com.cui.service.CourseService;
import com.cui.service.SpeakerService;
import com.cui.service.SubjectService;
import com.cui.service.VideoService;
import com.cui.utils.InfoUtils;
import com.cui.utils.UUIDUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/video")
public class VideoController {
    @Autowired
    VideoService videoService;
    @Autowired
    SpeakerService speakerService;
    @Autowired
    CourseService courseService;
    @Autowired
    SubjectService subjectService;

    @RequestMapping("/list")
    public String videoList(VideoQueryVo queryVo, Model model, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer pageSize) {
        System.out.println(queryVo);
        List<Speaker> speakers = speakerService.selectSpeakerAll();
        List<Video> videos = videoService.selectVideoAll(queryVo, page, pageSize);
        List<Course> courses = courseService.selectCourseAll();
        PageInfo<Video> pageInfo = new PageInfo<>(videos);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("speakerList", speakers);
        model.addAttribute("courseList", courses);
        return "/behind/videoList";
    }

    @RequestMapping("/edit")
    public String edit(Integer id, Model model) {
        Video video = videoService.selectVideoById(id);
        List<Speaker> speakers = speakerService.selectSpeakerAll();
        List<Course> courses = courseService.selectCourseAll();
        model.addAttribute("speakerList", speakers);
        model.addAttribute("courseList", courses);
        model.addAttribute("video", video);
        return "/behind/addVideo";
    }

    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(Video video) {
        System.out.println(video);
        if (video.getId() != null && video.getId() != 0) {
            videoService.updateVideo(video);
        } else {
            videoService.insertVideo(video);
        }
        return "redirect:/video/list";
    }

    @RequestMapping("/addVideo")
    public String addVideo(Model model) {
        List<Speaker> speakers = speakerService.selectSpeakerAll();
        List<Course> courses = courseService.selectCourseAll();
        model.addAttribute("speakerList", speakers);
        model.addAttribute("courseList", courses);
        return "/behind/addVideo";
    }

    @RequestMapping("/delBatchVideos")
    public String delBatchVideos(VideoQueryVo queryVo) {
        videoService.delBatchVideos(queryVo);
        return "redirect:/video/list";
    }

    @RequestMapping("/videoDel")
    @ResponseBody
    public String videoDel(Integer id) {
        System.out.println(id);
        int i = videoService.videoDel(id);
        return i > 0 ? "success" : "fail";
    }

    @RequestMapping("/upload")
    @ResponseBody
    public String upload(MultipartFile image) {
        System.out.println(image.getOriginalFilename());
        String originalFilename = image.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String img_path = InfoUtils.getProperties("IMG_PATH");
        String uuid = UUIDUtils.getUUID();
        String newFileName = uuid + substring;
        try {
            image.transferTo(new File(img_path, newFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return InfoUtils.getProperties("IMG_URL") + "/" + newFileName;
    }

    @RequestMapping("/showVideo")
    public String showVideo(Integer videoId,String subjectName,Model model) {
        Video video = videoService.selectVideoById(videoId);

        Course course = courseService.selectCourseById(video.getCourseId());
        Subject subject = subjectService.selectSubjectByName(subjectName);
        model.addAttribute("video",video);
        model.addAttribute("subject",subject);
        model.addAttribute("course",course);
        return "/before/section";
    }

}
