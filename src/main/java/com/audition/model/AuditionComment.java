package com.audition.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AuditionComment {

    private int postId;
    private int id;
    private String name;
    private String email;
    private String body;

}
