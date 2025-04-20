package com.internlink.internlink.controller;
//NEW HR CONTROLLER
import com.internlink.internlink.model.Application;
import com.internlink.internlink.model.HRManager;
import com.internlink.internlink.model.Internship;
import com.internlink.internlink.service.HRManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hrmanager")
public class HRManagerController {

    @Autowired
    private HRManagerService hrManagerService;

    // Endpoint to create an HRManager account
    @PostMapping("/create")
    public HRManager createAccount(@RequestBody HRManager hrManager) {
        return hrManagerService.createAccount(hrManager);
    }

     // Endpoint to view applications associated with an HRManager
     @GetMapping("/{id}/applications")
     public List<Application> viewApplications(@PathVariable String id) {
         return hrManagerService.viewApplications(id);
     }


      // Endpoint to announce a new internship opportunity
    @PostMapping("/{id}/announce")
    public Internship announceInternship(@PathVariable String id, @RequestBody Internship internshipOpportunity) {
        return hrManagerService.announceInternship(id, internshipOpportunity);
    }

// END HR CONTROLLER
} // end class 
