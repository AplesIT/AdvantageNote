package com.production.advangenote.helpers.count;

import com.production.advangenote.models.Note;

import rx.Observable;

/**
 * @author vietnh
 * @name IdeogramsWordCounter
 * @date 1/16/21
 **/
public class IdeogramsWordCounter implements WordCounter {

    @Override
    public int countWords(Note note) {
        return countChars(note);
    }

    @Override
    public int countChars(Note note) {
        String titleAndContent = note.getTitle() + "\n" + note.getContent();
        return Observable
                .from(sanitizeTextForWordsAndCharsCount(note, titleAndContent).split(""))
                .filter(s -> !s.matches("\\s"))
                .count().toBlocking().single();
    }
}
