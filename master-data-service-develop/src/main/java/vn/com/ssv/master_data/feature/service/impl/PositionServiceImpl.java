package vn.com.ssv.master_data.feature.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.com.ssv.master_data.common.exception.BusinessException;
import vn.com.ssv.master_data.common.exception.ResourceNotFoundException;
import vn.com.ssv.master_data.common.response.DomainCode;
import vn.com.ssv.master_data.common.response.PageResponse;
import vn.com.ssv.master_data.feature.entity.Position;
import vn.com.ssv.master_data.feature.mapper.PositionMapper;
import vn.com.ssv.master_data.feature.model.request.PositionCreateRequest;
import vn.com.ssv.master_data.feature.model.request.PositionSearchRequest;
import vn.com.ssv.master_data.feature.model.request.PositionUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.PositionDetailResponse;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;
import vn.com.ssv.master_data.feature.repository.PositionRepository;
import vn.com.ssv.master_data.feature.service.PositionService;

import java.util.List;

import static vn.com.ssv.master_data.feature.constant.Const.DELETED;
import static vn.com.ssv.master_data.feature.constant.Const.NOT_DELETED;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    PositionRepository positionRepository;
    PositionMapper positionMapper;

    @Override
    public PageResponse<PositionListResponse> list(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        Page<Position> positions = positionRepository.findByIsDeleted(NOT_DELETED, pageable);
        return PageResponse.of(positions.map(positionMapper::toListResponse));
    }

    @Override
    public List<PositionListResponse> listAll() {
        return positionRepository.findByIsDeletedOrderByIdAsc(NOT_DELETED)
                .stream()
                .map(positionMapper::toListResponse)
                .toList();
    }

    @Override
    public PositionDetailResponse getById(Long id) {
        Position position = findActivePosition(id);
        return positionMapper.toDetailResponse(position);
    }

    @Override
    public PositionDetailResponse create(PositionCreateRequest request) {
        if (positionRepository.existsByCodeAndIsDeleted(request.getCode(), NOT_DELETED)) {
            throw new BusinessException(DomainCode.CONFLICT, "Mã chức danh");
        }

        Position position = positionMapper.toEntity(request);
        position.setIsDeleted(NOT_DELETED);
        position = positionRepository.save(position);
        return positionMapper.toDetailResponse(position);
    }

    @Override
    public PositionDetailResponse update(Long id, PositionUpdateRequest request) {
        Position position = findActivePosition(id);

        positionRepository.findByCodeAndIsDeleted(request.getCode(), NOT_DELETED)
                .ifPresent(existingPosition -> {
                    if (!existingPosition.getId().equals(id)) {
                        throw new BusinessException(DomainCode.CONFLICT, "Mã chức danh");
                    }
                });

        positionMapper.updateEntity(position, request);
        position = positionRepository.save(position);
        return positionMapper.toDetailResponse(position);
    }

    @Override
    public void delete(Long id) {
        Position position = findActivePosition(id);
        position.setIsDeleted(DELETED);
        positionRepository.save(position);
    }

    @Override
    public PageResponse<PositionListResponse> search(PositionSearchRequest request) {
        return PageResponse.of(positionRepository.search(request));
    }

    private Position findActivePosition(Long id) {
        return positionRepository.findByIdAndIsDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(DomainCode.NOT_FOUND, "Chức danh"));
    }
}
