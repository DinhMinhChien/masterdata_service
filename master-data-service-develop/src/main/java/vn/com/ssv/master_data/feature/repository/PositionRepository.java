package vn.com.ssv.master_data.feature.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.ssv.master_data.common.persistence.BaseRepository;
import vn.com.ssv.master_data.feature.entity.Position;
import vn.com.ssv.master_data.feature.repository.custom.PositionRepositoryCustom;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends BaseRepository<Position, Long>, PositionRepositoryCustom {
    boolean existsByCodeAndIsDeleted(String code, Integer isDeleted);
    Optional<Position> findByCodeAndIsDeleted(String code, Integer isDeleted);
    Optional<Position> findByIdAndIsDeleted(Long id, Integer isDeleted);
    Page<Position> findByIsDeleted(Integer isDeleted, Pageable pageable);
    List<Position> findByIsDeletedOrderByIdAsc(Integer isDeleted);
}
