package com.leesh.devlab.domain.tag;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.post_tag.PostTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tags", indexes = {
        @Index(columnList = "name"),
})
@Entity
public class Tag extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", length = 30, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PostTag> postTags = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tag tag)) return false;
        return name != null && name.equals(tag.name);
    }

    private Tag(String name) {
        this.name = name;
    }

    public static Tag from(String name) {
        return new Tag(name);
    }
}
