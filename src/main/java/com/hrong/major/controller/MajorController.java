package com.hrong.major.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hrong.major.annotation.ClickLog;
import com.hrong.major.constant.CacheConstant;
import com.hrong.major.controller.common.BaseController;
import com.hrong.major.model.ClickType;
import com.hrong.major.model.Major;
import com.hrong.major.model.Search;
import com.hrong.major.model.Subject;
import com.hrong.major.model.vo.MajorVo;
import com.hrong.major.model.vo.SearchVo;
import com.hrong.major.service.MajorService;
import com.hrong.major.service.SearchService;
import com.hrong.major.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 专业 前端控制器
 * </p>
 *
 * @author hrong
 * @since 2019-08-13
 */
@Slf4j
@Controller
public class MajorController extends BaseController<Major> {
	@Resource
	private MajorService majorService;
	@Resource
	private SubjectService subjectService;
	@Resource
	private SearchService searchService;
	@Resource
	private CacheConstant cacheConstant;

	/**
	 * 分页获取专业信息
	 */
	@ClickLog(type = ClickType.majors)
	@GetMapping(value = "/majors/{subjectId}")
	public String getMajors(Model model, @PathVariable(value = "subjectId") int subjectId,
							@RequestParam(value = "page") int page,
							@RequestParam(value = "size") int size) {
		IPage<Major> majors = majorService.page(new Page<>(page, size), new QueryWrapper<Major>().eq("subject_id", subjectId).orderByAsc("order_number"));
		//翻页需要知道当前是什么科目类别
		Subject currentSubject = subjectService.getById(subjectId);
		List<Subject> subjects = cacheConstant.subjects();
		packagePageResult(model, majors);
		model.addAttribute("majors", majors.getRecords());
		model.addAttribute("data", null);
		model.addAttribute("currentSubject", currentSubject);
		model.addAttribute("subjects", subjects);
		model.addAttribute("searchVo", new SearchVo());
		model.addAttribute("searches", searchService.getPopularSearches());
		log.info("分页获取{}下的专业", currentSubject.getName());
		return "major/major";
	}

	/**
	 * 根据输入模糊查询专业
	 */
	@ClickLog(type = ClickType.majors)
	@PostMapping(value = "/majors")
	public String queryMajorsByShortName(Model model, @ModelAttribute SearchVo major){
		List<MajorVo> majors = majorService.findMajorsByName(major.getName());
		log.info("模糊查询专业信息，输入：{}， 查询结果数量：{}", major.getName(), majors.size());
		Search search = searchService.getOne(new QueryWrapper<Search>().eq("name", major.getName()));
		if (search == null) {
			search = new Search();
			search.setName(major.getName());
			search.setSearchCount(1);
			log.info("首次出现该搜索词:{}", major.getName());
		}else {
			Integer count = search.getSearchCount();
			search.setSearchCount(count + 1);
			log.info("搜索词：{}的搜索次数:{}", major.getName(), count);
		}
		searchService.saveOrUpdate(search);
		Subject subject = new Subject();
		subject.setId(0);
		subject.setName(major.getName());
		model.addAttribute("currentSubject", subject);
		model.addAttribute("data", majors);
		//启用与专业类别一起展示的方式
		model.addAttribute("majors", null);
		//不显示分页按钮
		model.addAttribute("totalPages", null);
		model.addAttribute("subjects", cacheConstant.subjects());
		model.addAttribute("searches", searchService.getPopularSearches());
		return "major/major";
	}

}

