package vn.com.ssv.master_data.feature.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.ssv.master_data.feature.entity.OrganizationType;

import java.util.Optional;

public interface OrganizationTypeRepository extends JpaRepository<OrganizationType,Long> {
    Optional<OrganizationType> findByCode(String code);
}
