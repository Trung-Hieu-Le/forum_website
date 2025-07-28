// package com.example.forum_website.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.CookieValue;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.ResponseBody;

// import com.example.forum_website.dto.ChangePasswordDto;
// import com.example.forum_website.dto.ChangeProfileDto;
// import com.example.forum_website.model.User;
// import com.example.forum_website.service.UserService;

// import jakarta.transaction.Transactional;

// @Controller
// public class UserController {
//     @Autowired
//     private UserService userService;

//     @GetMapping("/profile")
//     public String getProfile(@CookieValue("userId") Long userId, Model model) {
//         User user = null;
//         try {
//             user = userService.getUserById(userId);
//         } catch (Exception e) {
//             model.addAttribute("error", e.getMessage());
//             return "redirect:/";
//         }
//         model.addAttribute("user", user);
//         return "client/profile";
//     }

//     @PutMapping("/api/profile/update")
//     @ResponseBody
//     @Transactional
//     public ResponseEntity<?> updateProfile(@CookieValue("userId") Long userId, @RequestBody ChangeProfileDto changeProfileDto) {
//         try {
//             User updatedUser = userService.updateUserProfile(userId, changeProfileDto);
//             return ResponseEntity.ok(updatedUser);
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(e.getMessage());
//         }
//     }

//     @PostMapping("/api/profile/change-password")
//     @ResponseBody
//     @Transactional
//     public ResponseEntity<?> changePassword(@CookieValue("userId") Long userId, @RequestBody ChangePasswordDto changePasswordDto) {
//         try {
//             userService.changePassword(userId, changePasswordDto);
//             return ResponseEntity.ok().build();
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(e.getMessage());
//         }
//     }
// }
