package de.mherrmann.famkidmem.backend.body.admin;


import de.mherrmann.famkidmem.backend.body.ResponseBody;
import de.mherrmann.famkidmem.backend.entity.Video;

import java.util.List;

public class ResponseBodyGetVideos extends ResponseBody {

    private List<Video> videos;

    public ResponseBodyGetVideos(){}

    public ResponseBodyGetVideos(List<Video> videos){
        super("ok", "Successfully got videos");
        this.setVideos(videos);
    }

    public ResponseBodyGetVideos(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
