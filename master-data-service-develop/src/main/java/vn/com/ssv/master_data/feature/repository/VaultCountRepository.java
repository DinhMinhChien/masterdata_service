package vn.com.ssv.master_data.feature.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.ssv.master_data.common.persistence.BaseRepository;
import vn.com.ssv.master_data.feature.entity.VaultCount;

import java.util.Optional;

@Repository
public interface VaultCountRepository extends BaseRepository<VaultCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM VaultCount a WHERE a.type = :type AND a.isDeleted = :isDeleted")
    Optional<VaultCount> findByTypeAndIsDeletedForUpdate(String type, Integer isDeleted);
}
