package mall.dao;

import mall.entity.Notice;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeMapper {

    int getNoticeNoReadCount(@Param("userId") Long userId);

    List<Notice> findNoticeList(PageQueryUtil pageUtil);

    int getTotalNotices(PageQueryUtil pageUtil);

    Notice selectByPrimaryKey(Long id);

    int deleteByPrimaryKey(Long id);

    int insert(Notice record);

    int insertSelective(Notice record);

    int updateByPrimaryKeySelective(Notice record);

    int updateByPrimaryKey(Notice record);

    int readed(@Param("id") Long id);

    int updateText(@Param("id") Long id, @Param("title") String title, @Param("notice") String notice);
}