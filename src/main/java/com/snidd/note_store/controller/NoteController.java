package com.snidd.note_store.controller;


import com.snidd.note_store.entity.Note;
import com.snidd.note_store.CreateNoteRequest;
import com.snidd.note_store.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }


    /**
     * Logged-in user creates a note.
     * @param noteRequest request note.
     * @param authentication authentication object holding
     *                       authenticated user principals.
     * @return HttpStatus.
     */
    @PostMapping
    public ResponseEntity<String> createNote(
            @RequestBody CreateNoteRequest noteRequest,
            Authentication authentication) {

        // Authentication object holds token or
        // authenticated principal/user credentials.
        String username = authentication.getName();

        noteService.createNote(noteRequest, username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Note created.");
    }


    /**
     * Get a user's all notes.
     * @param authentication authentication object holding
     *                       authenticated user principals.
     * @return list of notes, if any.
     */
    @GetMapping()
    public List<Note> getNotes(Authentication authentication) {
        // Authentication object holds token or
        // authenticated principal/user credentials.
        String username = authentication.getName();

        return noteService.getNotes(username);
    }


    /**
     * Delete note by passing note id.
     * @param id note id.
     * @param authentication authentication object holding
     *                       authenticated user principals.
     * .noContent() will return a 204 status code, the request
     * was successful but no content is returned.
     * @return HttpStatus.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id, Authentication authentication) {

        String username = authentication.getName();
        noteService.deleteNote(id, username);
        return ResponseEntity.noContent().build();
    }


    /**
     *
     * @param noteRequest new note title and content.
     * @param id note id.
     * @param authentication authentication object holding
     *                       authenticated user principals.
     * @return HttpStatus.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateNote(
            @RequestBody CreateNoteRequest noteRequest,
            @PathVariable Long id, Authentication authentication) {

        String username = authentication.getName();
        noteService.updateNote(id, username, noteRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Note updated.");
    }


}
