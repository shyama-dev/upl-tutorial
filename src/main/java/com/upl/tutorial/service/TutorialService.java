package com.upl.tutorial.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upl.tutorial.dto.TutorialManageRequest;
import com.upl.tutorial.dto.TutorialRequest;
import com.upl.tutorial.dto.TutorialResponse;
import com.upl.tutorial.exception.EntityNotFoundException;
import com.upl.tutorial.model.Course;
import com.upl.tutorial.model.Tutorial;
import com.upl.tutorial.model.TutorialHistory;
import com.upl.tutorial.model.Users;
import com.upl.tutorial.repository.CourseRepo;
import com.upl.tutorial.repository.TutorialHistoryRepo;
import com.upl.tutorial.repository.TutorialRepository;
import com.upl.tutorial.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TutorialService {

    private final TutorialRepository tutorialRepo;

    private final CourseRepo courseRepo;

     private final UserRepository userRepo;

     private final TutorialHistoryRepo tutorialHistoryRepo;


    public int create(TutorialRequest request) {
        Tutorial tutorial =new Tutorial();
        Course course =courseRepo.findById(request.getCourseId()).orElseThrow(()->
        new EntityNotFoundException("Course not found for id :"+request.getCourseId()));
        tutorial.setCourse(course);
        tutorial.setTitle(request.getTitle());
        tutorial.setyoutubeLink(request.getYoutubeLink());
        tutorial.setContent(request.getContent());
        tutorial.setcreatedAt(LocalDateTime.now());
        Tutorial savedTutorial=tutorialRepo.save(tutorial);
        return savedTutorial.gettutorialId();

    }

    public List<TutorialResponse> fetchTutorials(int courseId) {

        List<Tutorial> tutorials =tutorialRepo.findByCourse_CourseId(courseId);
        List<TutorialResponse> tutorialsList = tutorials.stream().map(tutorial -> 
            new TutorialResponse(tutorial.gettutorialId(),courseId,tutorial.getTitle(),
            tutorial.getContent(),tutorial.getyoutubeLink(),tutorial.getcreatedAt())).toList();

        return tutorialsList;
    }

    @Transactional
    public void updateTutorial(TutorialManageRequest request) {

    Tutorial tutorial =tutorialRepo.findById(request.getTutorialId()).orElseThrow(()->
        new EntityNotFoundException("Tutorial not found for id :"+request.getTutorialId()));
        
        if (null != request.getContent() && !request.getContent().isBlank())
        tutorial.setContent(request.getContent());

        if (null != request.getTitle() && !request.getTitle().isBlank())
            tutorial.setTitle(request.getTitle());

        if (null != request.getYoutubeLink() && !request.getYoutubeLink().isBlank())
            tutorial.setyoutubeLink(request.getYoutubeLink());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String loggedInUSerEmail = userDetails.getUsername();
         Users user =userRepo.findByEmail(loggedInUSerEmail).orElseThrow(()->
        new EntityNotFoundException("Instructor not found for :"+loggedInUSerEmail));

        TutorialHistory tutorialHistory= new TutorialHistory();
        tutorialHistory.setTutorial(tutorial);
        tutorialHistory.setInstructor(user);
        tutorialHistory.setChanges(request.getChanges());
        tutorialHistory.setmodifiedAt(LocalDateTime.now());
        tutorialHistoryRepo.save(tutorialHistory);
    }


    
}
