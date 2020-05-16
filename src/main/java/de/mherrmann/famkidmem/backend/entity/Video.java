package de.mherrmann.famkidmem.backend.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Video {

    @Id
    private String id;

    private String title;
    private String description;
    private int durationInSeconds;
    private boolean recordedInCologne;
    private boolean recordedInGardelgen;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Year> years;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Person> persons;

    @OneToOne
    @JoinColumn(name = "key_id", referencedColumnName = "id")
    private Key key;

    @OneToOne
    @JoinColumn(name = "thumbnail_id", referencedColumnName = "id")
    private FileEntity thumbnail;

    @OneToOne
    @JoinColumn(name = "m3u8_id", referencedColumnName = "id")
    private FileEntity m3u8;

    private Video(){}

    public Video(
            String title, String description, int durationInSeconds, boolean recordedInCologne, boolean recordedInGardelgen,
            List<Year> years, List<Person> persons, Key key, FileEntity thumbnail, FileEntity m3u8) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.durationInSeconds = durationInSeconds;
        this.recordedInCologne = recordedInCologne;
        this.recordedInGardelgen = recordedInGardelgen;
        this.years = years;
        this.persons = persons;
        this.key = key;
        this.thumbnail = thumbnail;
        this.m3u8 = m3u8;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public boolean isRecordedInCologne() {
        return recordedInCologne;
    }

    public void setRecordedInCologne(boolean recordedInCologne) {
        this.recordedInCologne = recordedInCologne;
    }

    public boolean isRecordedInGardelgen() {
        return recordedInGardelgen;
    }

    public void setRecordedInGardelgen(boolean recordedInGardelgen) {
        this.recordedInGardelgen = recordedInGardelgen;
    }

    public List<Year> getYears() {
        return years;
    }

    public void setYears(List<Year> years) {
        this.years = years;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public FileEntity getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(FileEntity thumbnail) {
        this.thumbnail = thumbnail;
    }

    public FileEntity getM3u8() {
        return m3u8;
    }

    public void setM3u8(FileEntity m3u8) {
        this.m3u8 = m3u8;
    }
}
