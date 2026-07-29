package vn.com.ssv.master_data.feature.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.ssv.master_data.feature.constant.Const;
import vn.com.ssv.master_data.feature.entity.VaultCount;
import vn.com.ssv.master_data.feature.repository.VaultCountRepository;
import vn.com.ssv.master_data.feature.service.VaultCountService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VaultCountServiceImpl implements VaultCountService {
    VaultCountRepository vaultCountRepository;
    @Override
    public String generateCode(String type) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        long lastNumber = 0;
        Optional<VaultCount> vaultCount = vaultCountRepository.
                findByTypeAndIsDeletedForUpdate(type, Const.NOT_DELETED);
        if (vaultCount.isPresent()) {
            //neu ton tai thi kiem tra date xem dung nam thang khong
            VaultCount vaultCountExisted = vaultCount.get();
            if (Objects.equals(vaultCountExisted.getDate(), date)) {
                //dung thi = count
                lastNumber = vaultCountExisted.getCount();
            } else {
                //sai( sang thang moi) thi set date moi va lastNumber = 0
                vaultCountExisted.setDate(date);
            }
            vaultCountExisted.setCount(lastNumber + 1);
            vaultCountRepository.save(vaultCountExisted);
        } else {
            // neu luong chua ton tai thi them moi
            VaultCount vaultCountNew = new VaultCount();
            vaultCountNew.setDate(date);
            vaultCountNew.setCount(lastNumber + 1);
            vaultCountNew.setType(type);
            vaultCountNew.setIsDeleted(Const.NOT_DELETED);
            vaultCountRepository.save(vaultCountNew);
        }
        // nếu định dạng code khác thì switch case ở đây
        // định dạng mặc định type=JOB + date(tháng+năm)=0725+ count=0 => JOB072600001
        return type + date + String.format("%05d", lastNumber);
    }
}

