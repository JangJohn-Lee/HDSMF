package com.hdsmf.repository;

        import com.hdsmf.entity.RackDetailInfo;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.jpa.repository.Query;
        import org.springframework.stereotype.Repository;

@Repository
public interface RackSearchRepository extends JpaRepository<RackDetailInfo, Long> {

    //Rack별 row의 개수
    @Query(value = "SELECT count(distinct (r.rowNo)) FROM RackDetailInfo r WHERE r.rackNo = :rackNo")
    Integer selectRowNo(long rackNo);

    //Rack별 column의 개수
    @Query(value = "SELECT count(distinct (r.columnNo)) FROM RackDetailInfo r WHERE r.rackNo = :rackNo")
    Integer selectColNo(long rackNo);

    //Rack 총 개수
    @Query(value = "SELECT count(r.rackName) FROM RackInfo r")
    Integer selectRackNo();
}
