package de.mherrmann.famkidmem.backend.body.content;

import de.mherrmann.famkidmem.backend.entity.Video;

import java.util.List;

public class ResponseBodyContentIndex extends ResponseBodyContent {

    private List<Video> videos;
    private List<String> persons;
    private List<Integer> years;

    private ResponseBodyContentIndex(){}

    public ResponseBodyContentIndex(String masterKey, List<Video> videos, List<String> persons, List<Integer> years) {
        super(masterKey);
        this.videos = videos;
        this.persons = persons;
        this.years = years;
    }

    public ResponseBodyContentIndex(Exception ex) {
        super(ex);
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }
}
