package com.company.www.repository.convenience;

import com.company.www.constant.staff.MailType;
import com.company.www.entity.convenience.Mail;
import com.company.www.entity.staff.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
    Mail save(Mail mail);
    Mail findByMailId(Long mailId);
    Mail findByTitle(String title);
    Page<Mail> findAllBySendStaff(Pageable pageable, Staff sendStaff);
    Page<Mail> findAllByReceiveStaff(Pageable pageable, Staff receiveStaff);
    Page<Mail> findAllBySendStaffOrReceiveStaff(Pageable pageable, Staff sendStaff, Staff receiveStaff);
    Page<Mail> findAllBySendStaffAndMailType(Pageable pageable, Staff sendStaff, MailType mailType);
    Page<Mail> findAllByReceiveStaffAndMailType(Pageable pageable, Staff receiveStaff, MailType mailType);
}
