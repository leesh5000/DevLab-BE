package com.leesh.devlab.service;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.domain.tag.TagRepository;
import com.leesh.devlab.exception.custom.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    public Set<Tag> getAll(Set<String> names) {

        // 태그 목록들을 모두 소문자로 변환한다.
        names = names.stream().map(String::toLowerCase).collect(Collectors.toSet());

        return getByParrele(names);

    }

    /**
     * <p>
     *     태그 목록들을 DB에서 조회하는데, 없으면 객체를 생성한 뒤 반환함으로써 값이 존재함을 보장한다. <br>
     *     태그 조회 시 성능향상을 위해 병렬로 처리한다.
     * </p>
     * @param names
     * @return
     */
    private Set<Tag> getByParrele(Set<String> names) {

        // 태그 목록을 담을 리스트를 생성한다.
        Set<Tag> tags = new HashSet<>();

        // 병렬로 동시에 실행시킬 CompletableFuture 리스트를 생성한다.
        Set<CompletableFuture<Boolean>> futures = new HashSet<>();

        // 태그 목록을 순회하면서, DB에 있으면 가져오고 없으면 생성한다.
        for (String name : names) {
            CompletableFuture<Boolean> findOrCreate = CompletableFuture
                    .supplyAsync(
                            () -> tagRepository
                                    .findByName(name)
                                    .orElseGet(() -> Tag.from(name)))
                    .thenApply(tags::add);

            futures.add(findOrCreate);
        }

        // CompletableFuture 들을 모두 실행한다.
        CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[names.size()]))
                .exceptionally(e -> {
                    throw new BusinessException(ErrorCode.POST_SAVE_FAILED, e.getMessage());
                })
                .join();

        return tags;
    }

}
