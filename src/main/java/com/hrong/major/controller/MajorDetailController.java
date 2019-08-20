package com.hrong.major.controller;


import com.hrong.major.annotation.ClickLog;
import com.hrong.major.model.ClickType;
import com.hrong.major.model.Major;
import com.hrong.major.model.Subject;
import com.hrong.major.model.vo.MajorDetailWithVideoVo;
import com.hrong.major.service.MajorDetailService;
import com.hrong.major.service.MajorService;
import com.hrong.major.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hrong
 * @since 2019-08-17
 */
@Slf4j
@Controller
@RequestMapping("/major/info")
public class MajorDetailController {

	@Resource
	private MajorDetailService majorDetailService;
	@Resource
	private SubjectService subjectService;
	@Resource
	private MajorService majorService;

	/**
	 * 根据专业id查询详情
	 */
	@ClickLog(type = ClickType.major)
	@GetMapping(value = "/{id}")
	public String getMajorInfoById(Model model, @PathVariable(value = "id") int id) {
		MajorDetailWithVideoVo detail = majorDetailService.findDetailVoById(id);
		Integer nextDetailId = majorDetailService.findNextMajorDetailIdByCurrentMajorDetailId(id);
		Subject currentSubject = subjectService.getById(majorService.getById(detail.getDetail().getMajorId()).getSubjectId());
		List<Major> majors = majorService.findAroundMajors(id);
		model.addAttribute("detailVo", detail);
		model.addAttribute("nextId", nextDetailId);
		//从详情查询subject下的majors
		model.addAttribute("currentSubject", currentSubject);
		//显示该专业前后几个专业
		model.addAttribute("majors", majors);
		return "major/major_detail";
	}
}
