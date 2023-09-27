package com.leesh.devlab.api.member.dao;

import com.leesh.devlab.api.member.dto.MyProfile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *     JPA나 QueryDSL로 해결되지 않거나, 엔티티의 모영과 관계없이 화면에 특화된 조회 쿼리를 처리하기 위한 데이터베이스 접근 객체<br>
 *     도메인 기능의 처리를 위해 DB에 접근하는 Repository 객체와의 구분을 위해 Dao라는 이름을 사용<br>
 * </p>
 */
@Mapper
@Repository
@Transactional(readOnly = true)
public interface MemberDao {

    MyProfile getMyProfile(Long memberId);

}
