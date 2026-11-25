package com.snidd.note_store.repository;

import com.snidd.note_store.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // Fetch all notes belonging to user id.
    List<Note> findByUserId(Integer userId);
}
