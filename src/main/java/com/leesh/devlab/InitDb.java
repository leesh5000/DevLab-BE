package com.leesh.devlab;

import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class InitDb implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        for (int i = 1; i <= 2; i++) {

            String id = "member" + i;

            Member member = Member.builder()
                    .loginId(id)
                    .password(passwordEncoder.encode(id))
                    .nickname(id)
                    .build();

            for (int j = 1; j <= 5; j++) {

                String title = "member" + i + "의 게시글" + j;
                String contents = "게시글" + j + "의 내용";
                Post post = member.posting(title, contents);
                member.like(post);

                for (int k = 1; k <= 3; k++) {

                    String commentContents = "member" + i + "의 게시글" + j + "에 대한 댓글" + k;
                    Comment comment = member.comment(post, commentContents);
                    member.like(comment);
                }
            }

            memberRepository.save(member);
        }


    }
}
