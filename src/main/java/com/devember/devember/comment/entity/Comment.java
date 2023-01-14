package com.devember.devember.comment.entity;

import com.devember.devember.comment.dto.CommentDto;
import com.devember.devember.user.entity.User;
import com.devember.devember.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment extends BaseEntity implements Serializable {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "content")
    private String content;

    public static Comment from(CommentDto.Request request){
        return Comment.builder()
                .content(request.getContents())
                .user(request.getUser())
                .build();
    }

    public void update (CommentDto.Request request){
        this.content = request.getContents();
        this.user = request.getUser();
    }
}
