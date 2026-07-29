package vn.com.ssv.master_data.common.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import vn.com.ssv.master_data.common.exception.ResourceNotFoundException;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    default T findByIdOrThrow(ID id) {

        return findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
