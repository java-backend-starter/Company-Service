package com.company.www.repository.work.human_resource;

import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayInfoRepository extends JpaRepository<PayInfo, Long> {
    PayInfo save(PayInfo payInfo);

    PayInfo findByWork(Work work);

    PayInfo findByRoleAndPosition(Role role, Position position);
}
