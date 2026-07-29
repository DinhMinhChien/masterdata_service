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
import vn.com.ssv.master_data.feature.entity.Organization;
import vn.com.ssv.master_data.feature.entity.OrganizationType;
import vn.com.ssv.master_data.feature.mapper.OrganizationMapper;
import vn.com.ssv.master_data.feature.model.request.OrganizationCreateRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationSearchRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationDetailResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationTreeResponse;
import vn.com.ssv.master_data.feature.repository.OrganizationRepository;
import vn.com.ssv.master_data.feature.repository.OrganizationTypeRepository;
import vn.com.ssv.master_data.feature.service.OrganizationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
      OrganizationRepository organizationRepository;
      OrganizationMapper organizationMapper;
      OrganizationTypeRepository organizationTypeRepository;
    //  Hiển thị danh sách
    @Override
    public PageResponse<OrganizationListResponse> finAll(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("id").ascending());
        Page<Organization> organizations = organizationRepository.findByIsDeleted(0, pageable);
        return PageResponse.of(organizations.map(organizationMapper::toListResponse));
    }
    // Hiển thị danh sách chi tiết
    @Override
    public OrganizationDetailResponse getById(Long id) {
        Organization organization = organizationRepository
                .findActiveById(id).orElseThrow(() -> new ResourceNotFoundException(DomainCode.NOT_FOUND, "Tổ chức"));
        return organizationMapper.toDetailResponse(organization);
    }
    // Tạo danh sách
    @Override
    @Transactional
    public OrganizationDetailResponse create(OrganizationCreateRequest request) {

        if (organizationRepository.existsByCode(request.getCode())) {
            throw new BusinessException(DomainCode.CONFLICT, "Mã tổ chức");
        }
        OrganizationType type = organizationTypeRepository
                .findByCode(request.getTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException(DomainCode.NOT_FOUND, "Loại tổ chức"));
        Organization parent = null;

        if (request.getParentCode() != null
                && !request.getParentCode().isBlank()) {

            parent = organizationRepository
                    .findByCode(request.getParentCode())
                    .orElseThrow(() -> new ResourceNotFoundException(DomainCode.NOT_FOUND, "Đơn vị cha "));
        }
        Organization organization = organizationMapper.toEntity(request);
        organization.setTypeId(type.getId());
        if (parent != null) {
            organization.setParentId(parent.getId());
        }
        if (parent == null) {
            organization.setPath(request.getCode());
        } else {
            organization.setPath(parent.getPath() + "/" + request.getCode());
        }
        organization.setIsDeleted(0);
        organizationRepository.save(organization);

        return organizationMapper.toDetailResponse(organization);
    }

    // Cập nhật danh sách
    @Override
    @Transactional
    public OrganizationDetailResponse update(Long id, OrganizationUpdateRequest request) {

        Organization organization = organizationRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        DomainCode.NOT_FOUND,
                        "Tổ chức không tồn tại"));

        // Kiểm tra trùng mã
        organizationRepository.findByCode(request.getCode())
                .ifPresent(org -> {
                    if (!org.getId().equals(id)) {
                        throw new BusinessException(
                                DomainCode.CONFLICT,
                                "Mã tổ chức");
                    }
                });

        // Kiểm tra loại tổ chức
        OrganizationType type = organizationTypeRepository.findByCode(request.getTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        DomainCode.NOT_FOUND,
                        "Loại tổ chức"));

        // Kiểm tra đơn vị cha
        Organization parent = null;

        if (request.getParentCode() != null
                && !request.getParentCode().isBlank()) {

            parent = organizationRepository.findByCode(request.getParentCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            DomainCode.NOT_FOUND,
                            "Đơn vị cha"));

            if (parent.getId().equals(id)) {
                throw new BusinessException(
                        DomainCode.CONFLICT,
                        "Đơn vị không thể là cha của chính nó");
            }
        }

        // Cập nhật dữ liệu từ request
        organizationMapper.updateEntity(organization, request);

        // Cập nhật type
        organization.setTypeId(type.getId());

        // Cập nhật parent
        organization.setParentId(
                parent != null ? parent.getId() : null
        );

        // Cập nhật path
        organization.setPath(
                parent == null
                        ? request.getCode()
                        : parent.getPath() + "/" + request.getCode()
        );

        organization = organizationRepository.save(organization);

        return organizationMapper.toDetailResponse(organization);
    }
    // CHức năng xóa
    @Override
    @Transactional
    public void delete(long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DomainCode.NOT_FOUND, "Tổ chức"));
        organization.setIsDeleted(1);
        organizationRepository.save(organization);
    }
    // Chức năng danh sách cây tổ chức
    @Override
    public List<OrganizationTreeResponse> getTree() {
        //  Lấy toàn bộ dữ liêu chưa bị xóa
        List<Organization> organizations = organizationRepository.findByIsDeleted(0);
        //  dùng để Map để tra cứu nhanh theo id
        Map<Long, OrganizationTreeResponse> nodeMap = new HashMap<>();
        // Khởi tạo danh sách chứa các node gốc , để lưu các node gốc của cây
        List<OrganizationTreeResponse> roots = new ArrayList<>();
        for (Organization organization : organizations) {
            // Tạo node
            OrganizationTreeResponse node = organizationMapper.toTreeResponse(organization);
            // Lưu node vào Map, phục vụ tra cứu
            nodeMap.put(organization.getId(), node);
        }
        // Ghép cây
        for (Organization organization : organizations) {
            OrganizationTreeResponse node = nodeMap.get(organization.getId());
     //  Nếu organization không có parent_id thì đưa vào danh sách root
            if (organization.getParentId() == null) {
                roots.add(node);
            } else {
                OrganizationTreeResponse parent = nodeMap.get(organization.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }
        return roots;
    }

    // Chức năng tìm kiếm
    @Override
    public PageResponse<OrganizationListResponse> search(
            OrganizationSearchRequest request) {

        Page<OrganizationListResponse> result =
                organizationRepository.search(request);

        return PageResponse.of(result);
    }

    @Override
    public PageResponse<OrganizationListResponse> searchTree(
            OrganizationSearchRequest request) {

        Page<OrganizationListResponse> result =
                organizationRepository.searchTree(request);

        return PageResponse.of(result);
    }

    @Override

    public PageResponse<OrganizationListResponse> getParents(Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());

        Page<Organization> organizations = organizationRepository.findParents(pageable);

        return PageResponse.of(organizations.map(organizationMapper::toListResponse)
        );
    }


    @Override
    public List<OrganizationListResponse> getChildren(String parentCode) {
        List<Organization> organizations= organizationRepository.findChildrenByParentCode(parentCode);
        return organizations.stream().map(organizationMapper::toListResponse).toList();
    }



}
