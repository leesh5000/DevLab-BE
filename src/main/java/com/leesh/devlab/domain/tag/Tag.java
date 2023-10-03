package com.leesh.devlab.domain.tag;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.hashtag.Hashtag;
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

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private final List<Hashtag> hashtags = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tag tag)) return false;
        return id != null && id.equals(tag.id);
    }

    public Tag(String name) {
        this.name = name;
    }

}
