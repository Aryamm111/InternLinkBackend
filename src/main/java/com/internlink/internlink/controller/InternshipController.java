// package com.internlink.internlink.controller;

// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestPart;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;

// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.internlink.internlink.model.Internship;
// import com.internlink.internlink.service.ApplicationService;
// import com.internlink.internlink.service.AuthService;
// import com.internlink.internlink.service.EmbeddingService;
// import com.internlink.internlink.service.InteractionService;
// import com.internlink.internlink.service.InternshipService;
// import com.internlink.internlink.service.StudentService;

// @RestController
// @RequestMapping("/api/internships")
// public class InternshipController {
// @Autowired
// private AuthService authService;
// @Autowired
// private ApplicationService applicationService;
// @Autowired
// private InteractionService interactionService;
// @Autowired
// private StudentService studentService;
// @Autowired
// private InternshipService internshipService;

// @Autowired
// private EmbeddingService embeddingService;

// @PreAuthorize("hasRole('HR_MANAGER')")
// @PostMapping(value = "/create", consumes =
// MediaType.MULTIPART_FORM_DATA_VALUE)
// public ResponseEntity<String> createInternship(
// @RequestPart("title") String title,
// @RequestPart("company") String company,
// @RequestPart("location") String location,
// @RequestPart("description") String description,
// @RequestPart("startDate") String startDateStr,
// @RequestPart("duration") String durationStr,
// @RequestPart("majors") String majorsJson,
// @RequestPart("requiredSkills") String skillsJson,
// @RequestPart("maxStudents") String maxStudentsStr,
// @RequestPart(value = "internshipPlanFile", required = false) MultipartFile
// planFile,
// @RequestPart(value = "internshipImage", required = false) MultipartFile
// image) {
// try {
// List<String> majors = new ObjectMapper().readValue(majorsJson, new
// TypeReference<>() {
// });
// List<String> requiredSkills = new ObjectMapper().readValue(skillsJson, new
// TypeReference<>() {
// });
// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"); //
// ✅ ISO 8601
// Date startDate = sdf.parse(startDateStr);

// System.out.println("startDate class: " + startDate.getClass().getName());

// int duration = Integer.parseInt(durationStr);
// int maxStudents = Integer.parseInt(maxStudentsStr);
// String ManagerId = authService.getAuthenticatedUserId();

// String text = description + " "
// + String.join(" ", requiredSkills) + " "
// + String.join(" ", majors);
// List<Float> embedding = embeddingService.generateEmbedding(text);

// Internship internship = new Internship(
// ManagerId,
// title,
// company,
// location,
// description,
// startDate,
// duration,
// majors,
// requiredSkills,
// maxStudents,
// planFile != null ? planFile.getBytes() : null,
// image != null ? image.getBytes() : null,
// embedding);

// internshipService.createInternship(internship);
// System.out.println("Internship startDate: " +
// internship.getStartDate().getClass().getName());

// return ResponseEntity.ok("Internship created successfully!");
// } catch (Exception e) {
// e.printStackTrace();
// return ResponseEntity.internalServerError().body("Error creating internship:
// " + e.getMessage());
// }
// }

// @GetMapping("/recommend")
// public ResponseEntity<Map<String, Object>> recommendInternships(
// @RequestParam String studentId,
// @RequestParam(defaultValue = "1") int page,
// @RequestParam(defaultValue = "5") int limit) {
// try {

// List<Float> studentEmbedding = studentService.getStudentEmbedding(studentId);
// System.out
// .println("Student embedding fetched: " + (studentEmbedding != null ?
// "Success" : "Null or empty"));

// List<Internship> allInternships =
// internshipService.getRecommendedInternships(studentEmbedding);

// int startIndex = (page - 1) * limit;
// int endIndex = Math.min(startIndex + limit, allInternships.size());

// List<Internship> internshipsForPage = allInternships.subList(startIndex,
// endIndex);

// Map<String, Object> response = new HashMap<>();
// response.put("internships", internshipsForPage);
// int totalPages = Math.max(1, (int) Math.ceil((double) allInternships.size() /
// limit));
// response.put("totalPages", totalPages);
// response.put("currentPage", page);

// return ResponseEntity.ok(response);
// } catch (Exception e) {
// System.err.println("Error in recommendInternships: " + e.getMessage());
// e.printStackTrace();
// return ResponseEntity.internalServerError().build();
// }
// }

// @GetMapping("/search")
// public List<Internship> searchInternships(
// @RequestParam(required = false) String title,
// @RequestParam(required = false) String major,
// @RequestParam(defaultValue = "1") int page,
// @RequestParam(defaultValue = "10") int size) {
// return internshipService.searchInternships(title, major, page, size);
// }

// @PreAuthorize("hasRole('HR_MANAGER')")
// @GetMapping("/uploaded")
// public ResponseEntity<List<Internship>> getUploadedInternships(@RequestParam
// String hrManagerId) {
// try {
// List<Internship> internships =
// internshipService.getUploadedInternships(hrManagerId);
// return ResponseEntity.ok(internships);
// } catch (Exception e) {
// e.printStackTrace();
// return ResponseEntity.internalServerError().body(null);
// }
// }

// @GetMapping("/{id}")
// public ResponseEntity<Internship> getInternshipById(@PathVariable String id)
// {
// try {
// Internship internship = internshipService.getInternshipById(id);
// if (internship != null) {
// return ResponseEntity.ok(internship);
// } else {
// return ResponseEntity.notFound().build();
// }
// } catch (Exception e) {
// System.err.println("Error fetching internship details: " + e.getMessage());
// e.printStackTrace();
// return ResponseEntity.internalServerError().build();
// }
// }

// }
