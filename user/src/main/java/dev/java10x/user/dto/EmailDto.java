package dev.java10x.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

    private UUID userId;
    private String emailTo;
    private String emailSubject;
    private String body;



}
