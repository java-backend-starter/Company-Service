package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.Credit;
import com.company.www.entity.work.sales.Patron;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="debt")
@Getter
@Setter
public class Debt {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="debt_id")
    private Long debtId;

    @ManyToOne
    @JoinColumn(name="patron_id")
    private Patron patron;

    @Column(name="debt_type")
    private String debtType; // 채권, 채무

    @Column
    private String item;

    @Column
    private String type;

    @Column
    private long total;

    @Column
    private LocalDate start;

    @Column
    private LocalDate expire;

    @Column
    private String statement;

    @OneToMany(mappedBy="debt")
    private List<DebtHistory> debtHistory;

    @ManyToOne
    @JoinColumn(name="credit_id")
    private Credit credit;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

    public long getRest(){
        if(debtHistory.isEmpty()){
            return total;
        }
        else{
            long sum = 0;
            for(DebtHistory history : debtHistory){
                sum += history.getAmount();
            }
            return (total - sum);
        }
    }
    
}
