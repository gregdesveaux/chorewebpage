package com.chorewebpage.controller;

import com.chorewebpage.controller.dto.ChoreDto;
import com.chorewebpage.controller.dto.MarkDoneRequest;
import com.chorewebpage.config.KidDirectoryProperties;
import com.chorewebpage.service.ChoreService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chores")
public class ChoreController {

    private final ChoreService choreService;
    private final KidDirectoryProperties kidDirectoryProperties;

    public ChoreController(ChoreService choreService, KidDirectoryProperties kidDirectoryProperties) {
        this.choreService = choreService;
        this.kidDirectoryProperties = kidDirectoryProperties;
    }

    @GetMapping
    public List<ChoreDto> listChores() {
        return choreService.listChores().stream()
                .map(chore -> ChoreDto.fromEntity(chore, kidDirectoryProperties::getContactName))
                .toList();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ChoreDto> markDone(
            @PathVariable Long id, @Valid @RequestBody MarkDoneRequest request) {
        return choreService
                .markDone(id, request.getCompletedBy())
                .map(chore -> ResponseEntity.ok(ChoreDto.fromEntity(chore, kidDirectoryProperties::getContactName)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
