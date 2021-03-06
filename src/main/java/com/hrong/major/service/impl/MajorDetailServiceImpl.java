package com.hrong.major.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrong.major.dao.MajorDetailMapper;
import com.hrong.major.dao.MajorMapper;
import com.hrong.major.dao.VideoFeedbackMapper;
import com.hrong.major.dao.VideoMapper;
import com.hrong.major.model.Major;
import com.hrong.major.model.MajorDetail;
import com.hrong.major.model.Video;
import com.hrong.major.model.VideoFeedback;
import com.hrong.major.model.vo.MajorDetailVoWithSubject;
import com.hrong.major.model.vo.MajorDetailWithVideoVo;
import com.hrong.major.model.vo.VideoVo;
import com.hrong.major.service.MajorDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hrong
 * @since 2019-08-17
 */
@Slf4j
@Service
public class MajorDetailServiceImpl extends ServiceImpl<MajorDetailMapper, MajorDetail> implements MajorDetailService {

	@Resource
	private MajorDetailMapper majorDetailMapper;
	@Resource
	private VideoMapper videoMapper;
	@Resource
	private MajorMapper majorMapper;
	@Resource
	private VideoFeedbackMapper videoFeedbackMapper;

	@Override
	public MajorDetailWithVideoVo findDetailVoById(Serializable majorDetailId, String ip) {
		//获取当前页面的所有video
		MajorDetail detail = majorDetailMapper.selectById(majorDetailId);
		List<Video> videos = videoMapper.selectList(new QueryWrapper<Video>().eq("major_detail_id", majorDetailId)
				.eq("deleted", 0).orderByAsc("order_number"));
		List<VideoVo> videoVos = new ArrayList<>(videos.size());
		for (Video video : videos) {
			Integer videoId = video.getId();
			//查看当前用户是否点赞、踩与点击量...
			Integer isUpped = videoFeedbackMapper.selectCount(new QueryWrapper<VideoFeedback>().eq("video_id", videoId).eq("ip", ip).eq("type", "up"));
			Integer isDowned = videoFeedbackMapper.selectCount(new QueryWrapper<VideoFeedback>().eq("video_id", videoId).eq("ip", ip).eq("type", "down"));
			Integer clickCount = videoFeedbackMapper.selectCount(new QueryWrapper<VideoFeedback>().eq("video_id", videoId).eq("type", "click"));
			Integer upCount = videoFeedbackMapper.selectCount(new QueryWrapper<VideoFeedback>().eq("video_id", videoId).eq("type", "up"));
			Integer downCount = videoFeedbackMapper.selectCount(new QueryWrapper<VideoFeedback>().eq("video_id", videoId).eq("type", "down"));
			VideoVo videoVo = VideoVo.builder()
									.video(video).isDowned(isDowned)
									.isUpped(isUpped).clickCount(clickCount)
									.upCount(upCount).downCount(downCount)
									.build();
			videoVos.add(videoVo);
		}
		return MajorDetailWithVideoVo.builder().detail(detail).videos(videoVos).build();
	}

	@Override
	public Integer findNextMajorDetailIdByCurrentMajorDetailId(Serializable id) {
		//当前major
		Major currentMajor = majorMapper.selectById(id);
		//根据order_number排序后处于当前major后面的major
		Major nextMajor = majorMapper.selectOne(new QueryWrapper<Major>()
				.select("id", "name")
				.eq("subject_id", currentMajor.getSubjectId())
				.gt("order_number", currentMajor.getOrderNumber())
				.orderByAsc("order_number")
				.last("limit 1")
		);
		if (nextMajor == null) {
			log.info("{}已经是该类别下的最后一个专业", currentMajor.getName());
			return 0;
		}
		log.info("{}下一个专业为：{}", currentMajor.getName(), nextMajor.getName());
		MajorDetail nextMajorDetail = majorDetailMapper.selectOne(new QueryWrapper<MajorDetail>().eq("major_id", nextMajor.getId()));
		return nextMajorDetail == null ? 0 : nextMajorDetail.getId();
	}

	@Override
	public List<MajorDetailVoWithSubject> findMajorsDetailByNameAndSubjectName(Page page, String majorName, String subjectName) {
		return majorDetailMapper.findMajorsDetailByNameAndSubjectName(page, majorName, subjectName);
	}

	@Override
	public int countMajorsDetailByNameAndSubjectName(String majorName, String subjectName) {
		return majorDetailMapper.countMajorsDetailByNameAndSubjectName(majorName, subjectName);
	}
}
