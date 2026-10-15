package dev.java10x.user.controller;

import dev.java10x.user.domain.UserModel;
import dev.java10x.user.dto.UserDto;
import dev.java10x.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "Usuários", description = "Operações de gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário e publica o evento de criação.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserModel.class))),
            @ApiResponse(responseCode = "400", description = "Dados do usuário inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado", content = @Content)
    })
    public ResponseEntity<UserModel> createUser(@RequestBody UserDto userDto) {
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        UserModel savedUser = userService.createUser(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados.")
    @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserModel.class))))
    public ResponseEntity<List<UserModel>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Identificador do usuário", required = true)
            @PathVariable UUID id) {
        if (!userService.deleteUser(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
