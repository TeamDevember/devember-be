package com.devember.devember.comment.entity;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.comment.dto.CommentDto;
import com.devember.devember.entity.BaseEntity;
import com.devember.devember.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_card_id")
    private ProfileCard profileCard;

    private String content;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    List<Reply> replyList = new ArrayList<>();


    public void addReply(Reply reply){
        reply.setComment(this);
        replyList.add(reply);
    }


    public static Comment from(CommentDto.CreateRequest createRequest){
        return Comment.builder()
                .content(createRequest.getContents())
                .build();
    }

    public static Comment from(CommentDto.UpdateRequest updateRequest){
        return Comment.builder()
                .content(updateRequest.getContents())
                .build();
    }

}
