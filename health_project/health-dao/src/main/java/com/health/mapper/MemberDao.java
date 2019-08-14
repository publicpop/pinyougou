package com.health.mapper;

import com.github.pagehelper.Page;
import com.health.pojo.Member;

import java.util.List;

public interface MemberDao {
    public List<Member> findAll();
    public Page<Member> selectByCondition(String queryString);
    public void add(Member member);
    public void deleteById(Integer id);
    public Member findByTelephone(String telephone);
    public void edit(Member member);
    public Integer findMemberCountBeforeDate(String date);
    public Integer findMemberCountByDate(String date);
    public Integer findMemberCountAfterDate(String date);
    public Integer findMemberTotalCount();
    Member findById(Integer memberId);

    /**
    * 根据会员电话查询会员
    * @param telephone
    * @Return:
    */
    Member findMemberByTelephone(String telephone);
}
