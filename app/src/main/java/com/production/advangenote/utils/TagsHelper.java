
package com.production.advangenote.utils;

import androidx.core.util.Pair;

import com.production.advangenote.database.DAOSQL;
import com.production.advangenote.models.Note;
import com.production.advangenote.models.Tag;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import it.feio.android.pixlui.links.UrlCompleter;
import rx.Observable;

import static com.production.advangenote.utils.ConstantsBase.TAG_SPECIAL_CHARS_TO_REMOVE;


public class TagsHelper {


  public static List<Tag> getAllTags() {
    return DAOSQL.getInstance().getTags();
  }


  public static HashMap<String, Integer> retrieveTags(Note note) {
    HashMap<String, Integer> tagsMap = new HashMap<>();
    String[] words = (note.getTitle() + " " + note.getContent()).replaceAll("\n", " ").trim()
        .split(" ");
    for (String word : words) {
      String parsedHashtag = UrlCompleter.parseHashtag(word);
      if (StringUtils.isNotEmpty(parsedHashtag)) {
        int count = tagsMap.get(parsedHashtag) == null ? 0 : tagsMap.get(parsedHashtag);
        tagsMap.put(parsedHashtag, ++count);
      }
    }
    return tagsMap;
  }


  public static Pair<String, List<Tag>> addTagToNote(List<Tag> tags, Integer[] selectedTags,
                                                     Note note) {
    StringBuilder sbTags = new StringBuilder();
    List<Tag> tagsToRemove = new ArrayList<>();
    HashMap<String, Integer> tagsMap = retrieveTags(note);

    List<Integer> selectedTagsList = Arrays.asList(selectedTags);
    for (int i = 0; i < tags.size(); i++) {
      if (mapContainsTag(tagsMap, tags.get(i))) {
        if (!selectedTagsList.contains(i)) {
          tagsToRemove.add(tags.get(i));
        }
      } else {
        if (selectedTagsList.contains(i)) {
          if (sbTags.length() > 0) {
            sbTags.append(" ");
          }
          sbTags.append(tags.get(i));
        }
      }
    }
    return Pair.create(sbTags.toString(), tagsToRemove);
  }


  private static boolean mapContainsTag(HashMap<String, Integer> tagsMap, Tag tag) {
    for (String tagsMapItem : tagsMap.keySet()) {
      if (tagsMapItem.equals(tag.getText())) {
        return true;
      }
    }
    return false;
  }


  public static Pair<String, String> removeTag(String noteTitle, String noteContent,
                                               List<Tag> tagsToRemove) {
    String title = noteTitle, content = noteContent;
    for (Tag tagToRemove : tagsToRemove) {
      if (StringUtils.isNotEmpty(title)) {
        title = Observable.from(title.replaceAll(TAG_SPECIAL_CHARS_TO_REMOVE, " ").split("\\s"))
            .map(String::trim)
            .filter(s -> !s.matches(tagToRemove.getText()))
            .reduce((s, s2) -> s + " " + s2)
            .toBlocking()
            .singleOrDefault("");
      }
      if (StringUtils.isNotEmpty(content)) {
        content = Observable.from(content.replaceAll(TAG_SPECIAL_CHARS_TO_REMOVE, " ").split("\\s"))
            .map(String::trim)
            .filter(s -> !s.matches(tagToRemove.getText()))
            .reduce((s, s2) -> s + " " + s2)
            .toBlocking()
            .singleOrDefault("");
      }

    }
    return new Pair<>(title, content);
  }


  public static String[] getTagsArray(List<Tag> tags) {
    String[] tagsArray = new String[tags.size()];
    for (int i = 0; i < tags.size(); i++) {
      tagsArray[i] = tags.get(i).getText().substring(1) + " (" + tags.get(i).getCount() + ")";
    }
    return tagsArray;
  }


  public static Integer[] getPreselectedTagsArray(Note note, List<Tag> tags) {
    List<Integer> t = new ArrayList<>();
    for (String noteTag :  TagsHelper.retrieveTags(note).keySet()) {
      for (Tag tag : tags) {
        if (tag.getText().equals(noteTag)) {
          t.add(tags.indexOf(tag));
          break;
        }
      }
    }
    return t.toArray(new Integer[]{});
  }


  public static Integer[] getPreselectedTagsArray(List<Note> notes, List<Tag> tags) {
    HashSet<Integer> set = new HashSet<>();
    for (Note note : notes) {
      set.addAll(Arrays.asList(getPreselectedTagsArray(note, tags)));
    }
    return set.toArray(new Integer[]{});
  }
}
