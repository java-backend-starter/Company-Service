package com.company.www.repository.convenience;

import com.company.www.constant.staff.MemoStatus;
import com.company.www.constant.staff.MemoType;
import com.company.www.entity.convenience.Memo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    Memo save(Memo memo);
    Memo findByMemoId(Long memoId);
    Page<Memo> findAll(Pageable pageable);
    Page<Memo> findAllByTitleContainingIgnoreCase(Pageable pageable, String title);
    Page<Memo> findAllByContentContainingIgnoreCase(Pageable pageable, String content);
    Page<Memo> findAllByDate(Pageable pageable, LocalDate date);
    Page<Memo> findAllByMemoStatus(Pageable pageable, MemoStatus status);
    Page<Memo> findAllByMemoType(Pageable pageable, MemoType type);
}
