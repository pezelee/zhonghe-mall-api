package mall.service.impl;

import mall.api.admin.param.NoticeAddParam;
import mall.api.mall.vo.ZhongHeMallNoticeTitleVO;
import mall.api.mall.vo.ZhongHeMallNoticeVO;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.dao.MallUserMapper;
import mall.dao.NoticeMapper;
import mall.entity.MallUser;
import mall.entity.Notice;
import mall.service.NoticeService;
import mall.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private MallUserMapper userMapper;
    
    @Override
    public ZhongHeMallNoticeVO getNoticeDetailById(Long id) {
        Notice notice = noticeMapper.selectByPrimaryKey(id);
        if (notice == null) {
            ZhongHeMallException.fail(ServiceResultEnum.NOTICE_NOT_EXIST.getResult());
        }
        ZhongHeMallNoticeVO noticeVO = new ZhongHeMallNoticeVO();
        BeanUtil.copyProperties(notice,noticeVO);
        return noticeVO;
    }

    @Override
    public Boolean readed(Long id) {
        Notice notice = noticeMapper.selectByPrimaryKey(id);
        //当前通知非空才可以进行更改
        if (notice != null) {
            return noticeMapper.readed(id) > 0;
        }
        return false;
    }

    @Override
    public String deleteById(Long id) {
        Notice temp = noticeMapper.selectByPrimaryKey(id);
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setIsDeleted((byte) 1);
        if (noticeMapper.deleteByPrimaryKey(id) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PageResult getNoticeList(PageQueryUtil pageUtil) {

        List<Notice> notices;
        int total;

        notices = noticeMapper.findNoticeList(pageUtil);
        total = noticeMapper.getTotalNotices(pageUtil);

        PageResult pageResult = new PageResult(notices, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult getUserNoticeList(PageQueryUtil pageUtil) {

        List<Notice> notices;
        List<ZhongHeMallNoticeTitleVO> titleVOList;
        int total;

        notices = noticeMapper.findNoticeList(pageUtil);
        titleVOList = BeanUtil.copyList(notices,ZhongHeMallNoticeTitleVO.class);
        total = noticeMapper.getTotalNotices(pageUtil);

        PageResult pageResult = new PageResult(titleVOList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveNotice(NoticeAddParam addParam, Long userId) {


        MallUser user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        Notice notice = new Notice();
        notice.setTitle(addParam.getTitle());
        notice.setNotice(addParam.getNotice());
        notice.setSender(addParam.getSender());
        notice.setNoticeType(addParam.getNoticeType());

        notice.setUserId(user.getUserId());
        notice.setLoginName(user.getLoginName());
        notice.setNickName(user.getNickName());
        notice.setPhone(user.getPhone());
        notice.setOrganizationId(user.getOrganizationId());

        notice.setCreateTime(new Date());
        notice.setReadFlag((byte) 0);

        if (noticeMapper.insertSelective(notice) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }
}
