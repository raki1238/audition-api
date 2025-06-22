package com.audition.model;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;
    private List<AuditionComment> comments;

}
