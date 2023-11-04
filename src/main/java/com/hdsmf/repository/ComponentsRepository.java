package com.hdsmf.repository;

import com.hdsmf.entity.Components;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentsRepository extends JpaRepository<Components, String> {

    //전체 조회
    List<Components> findAll();

    //등록
    Components save(Components components);

    //수정
    Components saveAndFlush(Components components);

    //수정-조회(개별)
    Components findAllByComponentNo(@Param("componentNo") String componentNo);

    //삭제
    void deleteByComponentNo(@Param("componentNo") String componentNo);

    //품명 || 품번 조회
    @Query("SELECT c FROM Components c " +
            "WHERE (:searchField = '품번' AND c.componentNo LIKE CONCAT('%', :searchValue, '%')) " +
            "OR (:searchField = '품명' AND c.product LIKE CONCAT('%', :searchValue, '%'))")
    List<Components> searchComponentsByField(@Param("searchField") String searchField,
                                             @Param("searchValue") String searchValue);

    //유효성 검사(조회 - 일괄)
    @Query("SELECT count(c) FROM Components c WHERE c.componentNo = :componentNo")
    Integer selectComponentNoCheck(@Param("componentNo") String componentNo);

    //유효성 검사(components 삭제)
    @Query("SELECT count(i) FROM IOBound i WHERE i.componentNo = :componentNo")
    Integer selectIoBoundComponentNoCheck(@Param("componentNo") String componenetNo);

}
