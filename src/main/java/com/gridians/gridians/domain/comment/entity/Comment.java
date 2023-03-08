package com.gridians.gridians.domain.comment.entity;

import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.comment.dto.CommentDto;
import com.gridians.gridians.global.entity.BaseEntity;
import com.gridians.gridians.domain.user.entity.User;
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

    private String contents;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    List<Reply> replyList = new ArrayList<>();


    public void addReply(Reply reply){
        reply.setComment(this);
        replyList.add(reply);
    }

    public static Comment from(CommentDto.Request createRequest){
        return Comment.builder()
                .contents(createRequest.getContents())
                .build();
    }
}
