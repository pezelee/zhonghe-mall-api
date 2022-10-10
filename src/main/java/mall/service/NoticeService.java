
package mall.service;

import mall.api.admin.param.NoticeAddParam;
import mall.api.mall.vo.ZhongHeMallNoticeVO;
import mall.entity.Notice;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

public interface NoticeService {

    
    /**
     * 获取通知信息
     *
     * @param id
     * @return
     */
    ZhongHeMallNoticeVO getNoticeDetailById(Long id);

    /**
     * 通知信息改为已读
     *
     * @param id
     * @return
     */
    Boolean readed(Long id);

    
    /**
     * 删除接口
     * @param id
     * @return
     */
    String deleteById(Long id);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getNoticeList(PageQueryUtil pageUtil);

    /**
     * 用户通知列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getUserNoticeList(PageQueryUtil pageUtil);

    /**
     * 用户通知列表
     *
     * @param userId
     * @return
     */
    Integer getNoticeNoReadCount(Long userId);

    /**
     * 新增通知信息
     *
     *
     * @param noticeAddParam
     * @param userId
     * @return
     */
    String saveNotice(NoticeAddParam noticeAddParam, Long userId);
}
