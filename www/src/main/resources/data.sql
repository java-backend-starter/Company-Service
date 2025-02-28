--직위
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '미정', 0
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '회장', 140
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '부회장', 130
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '사장', 120
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '부사장', 110
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '전무이사', 100
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '상무이사', 90
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '이사', 80
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '부장', 70
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '차장', 60
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '과장', 50
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '대리', 40
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '주임', 30
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '사원', 20
);
insert into POSITIONS(
    POSITION_NAME, POSITION_RANK
)
values(
    '인턴', 10
);

commit;

-- 직무
insert into ROLE(
    ROLE_NAME
)
values (
    '미정'
);
insert into ROLE(
    ROLE_NAME
)
values (
    '공통'
);
insert into ROLE(
    ROLE_NAME
)
values (
    '경영진'
);
insert into ROLE(
    ROLE_NAME
)
values (
    '영업'
);
insert into ROLE(
    ROLE_NAME
)
values (
    '재무회계'
);
insert into ROLE(
    ROLE_NAME
)
values (
    '인사'
);
insert into ROLE(
    ROLE_NAME
)
values (
    '디자인'
);
insert into ROLE(
    ROLE_NAME
)
values (
    'IT'
);

commit;

--부서
insert into DEPARTMENT(
    DEPARTMENT_NAME,
    DEPARTMENT_ENGLISH_NAME
)
values(
    '미정',
    'not_department'
);
insert into DEPARTMENT(
    DEPARTMENT_NAME,
    DEPARTMENT_ENGLISH_NAME
)
values(
    '경영진',
    'directors'
);
insert into DEPARTMENT(
    DEPARTMENT_NAME,
    DEPARTMENT_ENGLISH_NAME
)
values(
    '영업부',
    'sales'
);
insert into DEPARTMENT(
    DEPARTMENT_NAME,
    DEPARTMENT_ENGLISH_NAME
)
values(
    '재무회계부',
    'account'
);
insert into DEPARTMENT(
    DEPARTMENT_NAME,
    DEPARTMENT_ENGLISH_NAME
)
values(
    '인사부',
    'human'
);
insert into DEPARTMENT(
    DEPARTMENT_NAME,
    DEPARTMENT_ENGLISH_NAME
)
values(
    '디자인부',
    'design'
);
insert into DEPARTMENT(
    DEPARTMENT_NAME,
    DEPARTMENT_ENGLISH_NAME
)
values(
    'IT부',
    'information'
);

commit;

-- 과
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 1),
    '미정'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 2),
    '경영진'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 3),
    '영업 1과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 3),
    '영업 2과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 4),
    '회계과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 4),
    '재무과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 5),
    '인사 1과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 5),
    '인사 2과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 6),
    '디자인 1과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 6),
    '디자인 2과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 7),
    'IT 1과'
);
insert into SECTION(
    DEPARTMENT,
    SECTION_NAME
)
values(
    (select department_name from department where department_id = 7),
    'IT 2과'
);

commit;

--영업부 업무
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '영업부 업무',
    '/sales/work'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '고객',
    '/sales/work/patron'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '고객 정보',
    '/patron/infomation'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '고객 신용 정보',
    '/patron/credit'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '견적서',
    '/sales/work/quotation'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '소프트웨어 견적서',
    '/quotation/software'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '개발 견적서',
    '/quotation/develop'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '주문 처리',
    '/sales/work/order'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '소프트웨어 주문',
    '/order/software'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '개발 주문',
    '/order/develop'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '소프트웨어',
    '/sales/work/software'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '소프트웨어 판매실적',
    '/software/sales'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 4),
    '대금 청구',
    '/sales/work/bill'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK

)
values (
    (select role_name from role where role_id = 4),
    '소프트웨어 대금',
    '/bill/software'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK

)
values (
    (select role_name from role where role_id = 4),
    '개발 대금',
    '/bill/develop'
);

-- 재무회계부 업무
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '재무회계부 업무',
    '/account/work'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '재무기록',
    '/account/work/financial'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '거래',
    '/financial/transaction'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '자산',
    '/financial/assetType'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '부채',
    '/financial/liability'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '자본',
    '/financial/capital'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '재무제표',
    '/financial/balance_sheet'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '손익계산서',
    '/financial/income'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '포괄손익계산서',
    '/financial/comprehensive_income'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '자본변동표',
    '/financial/equity_change'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '현금흐름표',
    '/financial/cash_flow'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '예산',
    '/account/work/budget'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '예산 계획',
    '/budget/plan'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '추가 예산 계획',
    '/budget/additional'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '세무',
    '/account/work/tax'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '법인세',
    '/tax/corporate'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '근로소득세',
    '/tax/payroll'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '부가가치세',
    '/tax/value_added'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '채권',
    '/account/work/receivable'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '채권 상환',
    '/receivable/history'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '채무',
    '/account/work/payable'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '채무 변제',
    '/payable/history'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '자산관리',
    '/account/work/asset'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '자산유형',
    '/asset/type'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '감가상각',
    '/asset/depreciation'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '원가관리',
    '/account/work/cost'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '표준원가',
    '/cost/standard'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '실제원가',
    '/cost/actual'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '의사결정 지원',
    '/account/work/decision'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '원가중심점',
    '/decision/cost_center'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '내부오더',
    '/decision/inner_order'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 5),
    '수익성 분석',
    '/account/work/profit'
);

-- 인사부 업무
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '인사부 업무',
    '/human/work'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '조직관리',
    '/human/work/organization'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '직무관리',
    '/human/work/role'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '직위관리',
    '/human/work/positions'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '인사행정',
    '/human/work/administrate'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '퇴사처리',
    '/administrate/check/resign'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '휴가관리',
    '/human/work/holiday'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 2),
    '휴가', -- 휴가 신청 및 결재(공통)
    '/common/holiday'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '휴가종류',
    '/holiday/type'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '휴가기록',
    '/holiday/record'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '휴가보고',
    '/holiday/report'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '근태기록',
    '/human/work/attendence'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 2),
    '출퇴근',
    '/common/work/commute'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '출퇴근 주간결산',
    '/commute/weekly'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '출퇴근 월간결산',
    '/commute/monthly'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '채용관리',
    '/human/work/recruit'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '채용공고',
    '/recruit/recruit_notice'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '지원자',
    '/recruit/applicant'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK

)
values (
    (select role_name from role where role_id = 6),
    '선발결과',
    '/recruit/qualify'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '교육관리',
    '/human/work/training'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '교육 프로그램',
    '/training/program'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '교육 대상자',
    '/training/trainee'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '교육 평가',
    '/training/appraisal'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '평가관리',
    '/human/work/appraisal'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '실적',
    '/appraisal/performance'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '승진&직무이동',
    '/human/work/change'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '급여 관리',
    '/human/work/pay'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '급여 정보',
    '/pay/information'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 6),
    '급여 명세서',
    '/pay/payroll'
);

-- 디자인부 업무
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    '디자인부 업무',
    '/design/work'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    'UI/UX 기획',
    '/design/work/plan'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    'UI/UX 분석',
    '/design/work/analysis'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    'UI/UX 설계',
    '/design/work/design'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    'UI/UX 작성',
    '/design/work/make'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    'UI/UX 검증',
    '/design/work/test'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    'UI/UX 결과물',
    '/design/work/output'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    '제품 디자인',
    '/design/work/product_design'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 7),
    '제품 디자인 평가',
    '/design/work/product_design_evaluate'
);
-- IT부 업무
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    'IT부 업무',
    '/information/work'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '기획',
    '/information/work/plan'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '요구사항 분석',
    '/information/work/analysis'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '기능&모듈 설계',
    '/information/work/design'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '기능&모듈 작성',
    '/information/work/coding'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '단위 테스트',
    '/information/work/unit_test'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '통합 테스트',
    '/information/work/integration_test'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '시스템 테스트',
    '/information/work/system_test'
);
insert into WORK_TYPE (
    ROLE,
    WORK_NAME,
    WORK_LINK
)
values (
    (select role_name from role where role_id = 8),
    '제품 결과물',
    '/information/work/output'
);

commit;

insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    1,
    '근로소득세',
    6,
    0,
    0,
    1400
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    2,
    '근로소득세',
    15,
    126,
    1400,
    5000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    3,
    '근로소득세',
    24,
    576,
    5000,
    8800
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    4,
    '근로소득세',
    35,
    1544,
    8800,
    15000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    5,
    '근로소득세',
    38,
    1994,
    15000,
    30000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    6,
    '근로소득세',
    40,
    2594,
    30000,
    50000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    7,
    '근로소득세',
    42,
    3594,
    50000,
    100000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    8,
    '근로소득세',
    45,
    6594,
    100000,
    0
);

insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    9,
    '법인세',
    9,
    0,
    0,
    20000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    10,
    '법인세',
    19,
    2000,
    20000,
    2000000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    11,
    '법인세',
    21,
    42000,
    2000000,
    30000000
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    12,
    '법인세',
    24,
    942000,
    30000000,
    0
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    13,
    '국민연금',
    4.5,
    0,
    0,
    0
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    14,
    '국민건강보험',
    3.43,
    0,
    0,
    0
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    15,
    '고용보험',
    0.8,
    0,
    0,
    0
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    16,
    '노인장기요양보험',
    5.76,
    0,
    0,
    0
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    17,
    '산업재해보상보험',
    1.5,
    0,
    0,
    0
);
insert into tax_type (
    tax_type_id,
    tax_name,
    tax,
    deduction,
    minimum,
    maximum
)
values (
    18,
    '부가가치세',
    10,
    0,
    0,
    0
);

commit;