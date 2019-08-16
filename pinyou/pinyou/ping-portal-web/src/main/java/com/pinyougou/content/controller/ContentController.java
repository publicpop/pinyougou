package com.pinyougou.content.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/content")
public class ContentController {

	@Reference
	private ContentService contentService;


	@RequestMapping("/findContent")
	/**
	* 轮播图
	* @param categoryId
	* @Return:
	*/
	public List<TbContent> findContent(Long categoryId) {
		return contentService.findContent(categoryId);
	}
}
