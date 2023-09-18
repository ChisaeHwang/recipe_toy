package com.oviesAries.recipe.domain.user.controller;

import com.oviesAries.recipe.domain.entity.User;
import com.oviesAries.recipe.domain.user.annotation.Authenticated;
import com.oviesAries.recipe.domain.user.annotation.Member;
import com.oviesAries.recipe.domain.user.application.AuthService;
import com.oviesAries.recipe.domain.user.domain.AuthPrincipal;
import com.oviesAries.recipe.domain.user.dto.request.LoginRequest;
import com.oviesAries.recipe.domain.user.dto.request.SignUpRequest;
import com.oviesAries.recipe.domain.user.dto.response.LoginResponse;
import com.oviesAries.recipe.domain.user.dto.response.SignupResponse;
import com.oviesAries.recipe.domain.user.service.UserService;
import com.oviesAries.recipe.domain.entity.UserIngredient;
import com.oviesAries.recipe.domain.user.dto.request.UserIngredientDTO;
import com.oviesAries.recipe.domain.user.dto.response.UserIngredientResponse;
import com.oviesAries.recipe.domain.utill.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> createUser(@Valid @RequestBody SignUpRequest request) {
        final Long memberId = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(SignupResponse.from(memberId));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest request) {
        final LoginResponse loginResponse = authService.loginMember(request);

        return ResponseEntity.ok(loginResponse);
    }

    @Member
    @PostMapping("/ingredients")
    public ResponseEntity<UserIngredientResponse> createUserIngredient(
            @Authenticated final AuthPrincipal authPrincipal,
            @RequestBody UserIngredientDTO request) {

        log.info("[Request] 장바구니 추가 : memberId = {}, productId = {}, quantity = {}",
                authPrincipal.getId(), request.getProductId(), request.getQuantity());
        User user = userService.addIngredient(request, authPrincipal.getId()).getUser();
        return ResponseEntity.created(URI.create("/cart-items/" + user.getId())).build();
    }

    @Member
    @GetMapping("/ingredients")
    public ResponseEntity<List<UserIngredientResponse>> getAllIngredient(
            @Authenticated final AuthPrincipal authPrincipal
            ) {

        List<UserIngredient> userIngredients = userService.getAllUserIngredient(authPrincipal.getId());

        List<UserIngredientResponse> response = userIngredients.stream()
                .map(UserMapper::toIngredientResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

//    @GetMapping("id/{id}")
//    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
//        User user = userService.getUserById(id);
//        if (user == null){
//            return ResponseEntity.notFound().build();
//        }
//        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getUserName(), user.getPassword());
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/{userId}/ingredients/{recipeIngredientId}")
    public ResponseEntity<UserIngredientResponse> deleteIngredient(
            @PathVariable Long userId,
            @PathVariable Integer recipeIngredientId) {
        userService.deleteUserIngredient(userId, recipeIngredientId);
        return ResponseEntity.noContent().build();
    }


}